package org.fuelteam.watt.test.lazy;

import java.util.concurrent.TimeUnit;

import org.fuelteam.watt.lucky.lazy.Lazy;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
public class LazyBenchmark {

    private Lazy<String> initializedLazy;

    private Lazy<String> uninitializedLazy;

    @Setup
    public void before() {
        initializedLazy = Lazy.create(() -> "Initialized");
        initializedLazy.get();
        uninitializedLazy = Lazy.create(() -> "Uninitialized");
    }

    @Benchmark
    public String benchmarkGet() {
        return initializedLazy.get();
    }

    @Benchmark
    public String benchmarkInitializeAndGet() {
        return uninitializedLazy.get();
    }

    public static void main(String... args) throws RunnerException {
        Options opts = new OptionsBuilder().include(".*").warmupIterations(10).measurementIterations(20)
                .jvmArgs("-Xms512m", "-Xmx512m").shouldDoGC(true).forks(1).build();
        new Runner(opts).run();
    }
}