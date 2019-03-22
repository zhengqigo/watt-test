package org.fuelteam.watt.test.window;

import java.util.Iterator;
import java.util.Spliterators;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@FunctionalInterface
public interface StreamableSupplier<T> {
    T get();

    static <T> StreamableSupplier<T> fromStream(Stream<T> stream) {
        Iterator<T> streamIterator = stream.iterator();
        return streamIterator::next;
    }

    static <T> StreamableSupplier<T> fromSupplier(Supplier<T> supplier) {
        return supplier::get;
    }

    default Stream<T> toStream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public T next() {
                return get();
            }
        }, 0), false);
    }
}