package dev.katpb.reactiveplayground.sec01.publisher;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

@Slf4j
public class SubscriptionImpl implements Subscription {

    private Subscriber<? super String> subscriber;
    private boolean cancelled;
    private final Faker faker;
    private static final int MAX_ITEMS = 10;
    private int itemCount = 0;

    public SubscriptionImpl(Subscriber<? super String> subscriber) {
        this.subscriber = subscriber;
        this.faker = Faker.instance();
    }

    @Override
    public void request(long l) {
        if (cancelled) {
            return;
        }
        if(l >= MAX_ITEMS) {
            cancelled = true;
            subscriber.onError(new IllegalStateException("Maximum items reached"));
            return;
        }
        log.info("Requesting subscription for [{}] items", l);
        for (long i = 0; i < l && itemCount < MAX_ITEMS; i++) {
            itemCount++;
            subscriber.onNext(faker.internet().emailAddress());
        }
        if(itemCount == MAX_ITEMS) {
            log.info("No more items to publish, subscription cancelled");
            subscriber.onComplete();
            cancelled = true;
        }
    }

    @Override
    public void cancel() {
        log.info("Subscription cancelled");
        cancelled = true;
    }
}
