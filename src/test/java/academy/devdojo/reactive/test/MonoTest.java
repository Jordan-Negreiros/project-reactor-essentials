package academy.devdojo.reactive.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Reative Streams 1.Asynchronous 2. Non-blocking 3. Backpressure Publisher <- (subscribe) Subscriber Subscription  is created Publisher (onSubscribe
 * with the subscription) -> Subscriber Subscription <- (request N) Subscriber Publisher -> (onNext) Subscriber until: 1. Publisher sends all the
 * objects requested. 2. Publisher sends all the objects it has. (onComplete) subscriber and subscrition will be canceled 3. There is an error.
 * (onError) -> subscriber and subscrition will be canceled
 */
@Slf4j
public class MonoTest {

    @Test
    public void monoSubscriber() {
        String name = "Jordan Negrerios";

        Mono<String> mono = Mono.just(name).log();
        log.info("Init mono.subscribe()");
        mono.subscribe();

        log.info("Init verifier");
        StepVerifier.create(mono)
            .expectNext(name)
            .verifyComplete();
    }

    @Test
    public void monoSubscriberConsumer() {
        String name = "Jordan Negrerios";

        Mono<String> mono = Mono.just(name).log();
        log.info("Init mono.subscribe()");
        mono.subscribe(string -> log.info("Value {}", string));

        log.info("Init verifier");
        StepVerifier.create(mono)
            .expectNext(name)
            .verifyComplete();
    }

    @Test
    public void monoSubscriberConsumerError() {
        String name = "Jordan Negrerios";

        Mono<String> mono = Mono.just(name)
            .map(s -> {
                throw new RuntimeException("Testing mono with error");
            });

        log.info("Init mono.subscribe()");

        mono.subscribe(
            string -> log.info("Name: {}", string),
            string -> log.error("Something bad happened"));

        mono.subscribe(
            string -> log.info("Name: {}", string),
            Throwable::printStackTrace);

        log.info("Init verifier");

        StepVerifier.create(mono)
            .expectError(RuntimeException.class)
            .verify();
    }

    @Test
    public void monoSubscriberConsumerComplete() {
        String name = "Jordan Negrerios";

        Mono<String> mono = Mono.just(name)
            .log()
            .map(String::toUpperCase);

        log.info("Init mono.subscribe()");

        mono.subscribe(
            string -> log.info("Value {}", string),
            Throwable::printStackTrace,
            () -> log.info("FINISHED!"));

        log.info("Init verifier");

        StepVerifier.create(mono)
            .expectNext(name.toUpperCase())
            .verifyComplete();
    }

    @Test
    public void monoSubscriberConsumerCompleteSubscription() {
        String name = "Jordan Negrerios";

        Mono<String> mono = Mono.just(name)
            .log()
            .map(String::toUpperCase);

        log.info("Init mono.subscribe()");

        mono.subscribe(
            string -> log.info("Value {}", string),
            Throwable::printStackTrace,
            () -> log.info("FINISHED!"),
            Subscription::cancel);

        log.info("Init verifier");

        StepVerifier.create(mono)
            .expectNext(name.toUpperCase())
            .verifyComplete();
    }

    @Test
    public void monoDoOnMethods() {
        String name = "Jordan Negrerios";

        Mono<Object> mono = Mono.just(name)
            .log()
            .map(String::toUpperCase)
            .doOnSubscribe(subscription -> log.info("Subscribed"))
            .doOnRequest(longNumber -> log.info("Request Received, starting doing something..."))
            .doOnNext(s -> log.info("Value is here. Executing doOnNext {}", s))
            .flatMap(s -> Mono.empty())
            .doOnSuccess(s -> log.info("doOnSuccess executed"));

        mono.subscribe(s -> log.info("Value {}", s),
            Throwable::printStackTrace,
            () -> log.info("FINISHED"));

        log.info("Init verifier");
    }
}
