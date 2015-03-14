package com.shadow.util;

import com.google.common.collect.Collections2;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 * @author nevermore on 2015/3/15.
 */
public class RandomUtil {

    public static <E> Optional<E> random(@Nonnull Collection<E> c, Function<E, Integer> rateProducer) {
        requireNonNull(rateProducer);
        if (CollectionUtils.isEmpty(c)) {
            return Optional.<E>empty();
        }
        int total = c.stream().mapToInt(rateProducer::apply).sum();
        int random = ThreadLocalRandom.current().nextInt(total);
        for (E e : c) {
            int rate = rateProducer.apply(e);
            if (rate > random) {
                return Optional.of(e);
            }
            random -= rate;
        }
        throw new IllegalStateException("should never happen.");
    }

    public static <E> Optional<E> random(@Nonnull Collection<E> c, @Nonnull Function<E, Integer> rateProducer,
                                         @Nonnull Predicate<E> predicate) {
        requireNonNull(rateProducer);
        requireNonNull(predicate);
        if (CollectionUtils.isEmpty(c)) {
            return Optional.<E>empty();
        }
        int total = c.stream().filter(predicate).mapToInt(rateProducer::apply).sum();
        int random = ThreadLocalRandom.current().nextInt(total);
        for (E e : Collections2.filter(c, predicate::test)) {
            int rate = rateProducer.apply(e);
            if (rate > random) {
                return Optional.of(e);
            }
            random -= rate;
        }
        throw new IllegalStateException("should never happen.");
    }
}
