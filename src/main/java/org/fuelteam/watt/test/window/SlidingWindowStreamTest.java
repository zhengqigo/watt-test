package org.fuelteam.watt.test.window;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.testng.collections.Lists;

import com.codepoetics.protonpack.StreamUtils;

class SlidingWindowStreamTest {

    @Test
    <T> void shouldApplySlidingWindow() {
        List<Integer> source = Lists.newArrayList(1, 2, 3, 4);

        List<List<Integer>> result = StreamUtils.windowed(source.stream(), 3).collect(toList());

        assertThat(result).containsExactly(Lists.newArrayList(1, 2, 3), Lists.newArrayList(2, 3, 4));
    }

    @Test
    void shouldApplySlidingWindowToStreamSmallerThanWindow() {
        List<Integer> source = Lists.newArrayList(1, 2);

        List<List<Integer>> result = StreamUtils.windowed(source.stream(), 3).collect(toList());

        assertThat(result).containsOnly(Lists.newArrayList(1, 2));
    }

    @Test
    void shouldApplySlidingWindowToEmptyStream() {
        List<Integer> source = Collections.emptyList();

        List<List<Integer>> result = StreamUtils.windowed(source.stream(), 3).collect(toList());

        assertThat(result).isEmpty();
    }

    @Test
    void shouldApplyZeroSlidingWindow() {
        List<Integer> source = Lists.newArrayList(1, 2, 3, 4);

        List<List<Integer>> result = StreamUtils.windowed(source.stream(), 0).collect(toList());

        assertThat(result).containsOnly(Lists.newArrayList(1, 2, 3, 4));
    }

    @Test
    void shouldDoNotLateBindToInternalBuffer() {
        List<Integer> source = Lists.newArrayList(1, 2, 3, 4);

        List<List<Integer>> result = StreamUtils.windowed(source.stream(), 2).collect(toList());

        Stream<Integer> s3 = result.get(2).stream();

        assertThat(s3.collect(toList())).containsExactly(3, 4);
    }

    @Test
    void shouldCalculateSize() {
        List<Integer> source = Lists.newArrayList(1, 2, 3, 4);

        Stream<List<Integer>> steam = StreamUtils.windowed(source.stream(), 3);
        Spliterator<List<Integer>> splitor = steam.spliterator();
        long result = splitor.estimateSize();
        assertThat(result).isEqualTo(1);
    }

    @Test
    void shouldEstimateSizeWhenWindowTooBig() {
        List<Integer> source = Lists.newArrayList(1, 2, 3, 4);

        long result = StreamUtils.windowed(source.stream(), source.size() + 1).spliterator().estimateSize();

        assertThat(result).isEqualTo(1);
    }
}