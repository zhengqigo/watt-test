package org.fuelteam.watt.test.timing;

import java.util.ArrayList;

import org.fuelteam.watt.lucky.timeing.SessionWindowCollection;
import org.fuelteam.watt.lucky.timeing.SlidingWindowCollection;
import org.fuelteam.watt.lucky.timeing.TimeWindow;
import org.fuelteam.watt.lucky.timeing.TumblingWindowCollection;
import org.junit.Assert;
import org.junit.Test;

public class WindowingTests {

    @Test
    public void testSessionWindowTimeout() {
        int numElements = 100;
        long timeout = 1000000000;
        long startTime = System.nanoTime();
        ArrayList<TestObject> test = new ArrayList<TestObject>();

        SessionWindowCollection<TestObject> swc = new SessionWindowCollection<>(test, (testObject) -> testObject.getTimestamp(),
                startTime, timeout);

        TestObject last = null;
        for (int i = 0; i < numElements; i++) {
            last = new TestObject(System.nanoTime());
            if (i % 25 == 0) {
                // wait some amount of time
                try {
                    System.out.println("waiting " + i);
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            swc.add(last);
        }
        Assert.assertTrue(test.size() == 100);
        long prev, cur;
        boolean first = true;
        prev = cur = 0;
        for (TimeWindow<TestObject> window : swc) {
            if (first) {
                prev = window.getEndTime();
                first = false;
                continue;
            } else {
                cur = window.getStartTime();
            }
            Assert.assertTrue(cur > (prev + timeout));
            prev = cur;
            for (TestObject t : window) {
                Assert.assertTrue(t.getTimestamp() >= window.getStartTime() && t.getTimestamp() <= window.getEndTime());
            }
        }
    }

    @Test
    public void testTumblingWindowDuration() {
        int numElements = 100;
        long start = System.nanoTime();
        long duration = 20000;
        ArrayList<TestObject> test = new ArrayList<TestObject>();

        TumblingWindowCollection<TestObject> swc = new TumblingWindowCollection<>(test, (testObject) -> testObject.getTimestamp(),
                duration, start);
        for (int i = 0; i < numElements; i++) {
            swc.add(new TestObject(System.nanoTime()));
        }
        Assert.assertTrue(test.size() == 100);
        int items = 0;
        for (TimeWindow<TestObject> window : swc) {
            Assert.assertTrue((window.getEndTime() - window.getStartTime()) <= duration);
            for (TestObject t : window) {
                Assert.assertTrue(t.getTimestamp() >= window.getStartTime() && t.getTimestamp() < window.getEndTime());
            }
        }

        System.out.println("total items: " + items);
    }

    @Test
    public void testSlidingWindowDuration() {
        int numElements = 100;
        long start = System.nanoTime();
        long duration = 20000;
        long every = 10000;
        ArrayList<TestObject> test = new ArrayList<TestObject>();

        SlidingWindowCollection<TestObject> swc = new SlidingWindowCollection<>(test, (testObject) -> testObject.getTimestamp(),
                duration, every, start);
        for (int i = 0; i < numElements; i++) {
            swc.add(new TestObject(System.nanoTime()));
        }
        Assert.assertTrue(test.size() == 100);
        for (TimeWindow<TestObject> window : swc) {
            Assert.assertTrue((window.getEndTime() - window.getStartTime()) <= duration);
            for (TestObject t : window) {
                Assert.assertTrue(t.getTimestamp() >= window.getStartTime() && t.getTimestamp() < window.getEndTime());
            }

        }
    }

    @Test
    public void testUtilsAdd() {
        int numElements = 100;
        long start = System.nanoTime();
        long duration = 20000;
        ArrayList<TestObject> tumblingSource = new ArrayList<TestObject>();

        TumblingWindowCollection<TestObject> twc = new TumblingWindowCollection<>(tumblingSource,
                (testObject) -> testObject.getTimestamp(), duration, start);
        for (int i = 0; i < numElements; i++) {
            twc.add(new TestObject(System.nanoTime()));
        }

        Assert.assertTrue(tumblingSource.size() == numElements);
    }

    @Test
    public void TestUtilsAddToFront() {
        ArrayList<TestObject> list = new ArrayList<>(25);
        long start = System.currentTimeMillis();
        long every = 1000L;
        long duration = 2500L;
        SlidingWindowCollection<TestObject> swc = new SlidingWindowCollection<TestObject>(list, test -> test.getTimestamp(),
                duration, every, start);
        long addToFront = System.currentTimeMillis();
        swc.add(new TestObject(System.currentTimeMillis() + 1000L));
        swc.add(new TestObject(addToFront));
        Assert.assertTrue(list.size() == 2);
        Assert.assertTrue(list.get(0).getTimestamp() == addToFront);
    }

    @Test
    public void testEviction() {
        int numElements = 100;
        long start = System.nanoTime();
        long duration = 20000;
        long middle = 0;
        ArrayList<TestObject> test = new ArrayList<TestObject>();

        TumblingWindowCollection<TestObject> swc = new TumblingWindowCollection<>(test, (testObject) -> testObject.getTimestamp(),
                20000, start);

        TestObject last = null;
        for (int i = 0; i < numElements; i++) {
            last = new TestObject(System.nanoTime());
            if (i == 50) {
                middle = last.getTimestamp();
            }
            swc.add(last);
        }
        System.out.println(test.size());
        System.out.println("last timestamp: " + last.getTimestamp());

        int i = 0;
        int items = 0;
        for (TimeWindow<TestObject> window : swc) {
            for (TestObject item : window) {
                items++;
            }
            i++;
        }
        Assert.assertTrue(i > 0);
        Assert.assertTrue(items == numElements);

        TumblingWindowCollection<TestObject> twcEvictHalf = new TumblingWindowCollection<>(test,
                testObject -> testObject.getTimestamp(), duration, middle);

        Assert.assertTrue(test.size() <= numElements / 2);

        TumblingWindowCollection<TestObject> twcEvict = new TumblingWindowCollection<>(test,
                testObject -> testObject.getTimestamp(), 20000, last.getTimestamp() + 100000000);

        Assert.assertTrue(test.size() == 0);
    }
}