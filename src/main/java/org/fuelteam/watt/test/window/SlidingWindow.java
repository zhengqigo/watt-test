package org.fuelteam.watt.test.window;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class SlidingWindow {

    public static void main(String args[]) {
        Stream<List<Integer>> stream = SlidingWindow
                .pagedStream(StreamableSupplier.fromStream(IntStream.range(1, 10000).boxed()), 5).apply(i -> i <= 100);
        stream.forEach(System.out::println);
    }

    public static <T> Function<Predicate<? super T>, Stream<List<T>>> pagedStream(StreamableSupplier<T> supplier,
            final int pageSize) {
        return pagedStream(supplier, pageSize, 1);
    }

    public static <T> Function<Predicate<? super T>, Stream<List<T>>> pagedStream(StreamableSupplier<T> supplier,
            final int pageSize, int slideAmount) {
        return pagedStream(supplier, pageSize, slideAmount, 0);
    }

    public static <T> Function<Predicate<? super T>, Stream<List<T>>> pagedStream(StreamableSupplier<T> supplier,
            final int pageSize, final int slideAmount, int offset) {
        return (stopPredicate) -> StreamSupport.stream(Spliterators.spliteratorUnknownSize(new Iterator<List<T>>() {
            LinkedList<T> window = new LinkedList<>();
            boolean hasFinished = false;
            {
                for (int i = 0; i < offset; i++) {
                    T temp = supplier.get();
                    if (!stopPredicate.test(temp)) {
                        hasFinished = true;
                        break;
                    }
                }
            }

            @Override
            public boolean hasNext() {
                return !hasFinished;
            }

            @Override
            public List<T> next() {
                if (hasFinished) {
                    throw new NoSuchElementException("The iterator is empty!");
                }

                hasFinished = populate();
                List<T> currWindow = new ArrayList<>(window);
                if (!hasFinished) {
                    hasFinished = slide();
                }
                return currWindow;
            }

            private boolean populate() {
                boolean added = false;
                while (window.size() < pageSize) {
                    T item = supplier.get();
                    if (!stopPredicate.test(item)) {
                        break;
                    }
                    window.add(item);
                    added = true;
                }

                if (added) {
                    return window.size() != pageSize;
                }
                window.clear();
                return false;
            }

            private boolean slide() {
                for (int p = 0; p < slideAmount; p++) {
                    if (!window.isEmpty()) {
                        window.pop();
                    } else if (!stopPredicate.test(supplier.get())) {
                        return true;
                    }
                }
                return false;
            }
        }, 0), false);
    }
}