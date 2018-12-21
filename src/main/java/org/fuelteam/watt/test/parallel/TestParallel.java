package org.fuelteam.watt.test.parallel;

import java.util.concurrent.ForkJoinPool;

import org.fuelteam.watt.lucky.parallel.DefaultForkJoinParallel;
import org.fuelteam.watt.lucky.parallel.ForkJoinPoolFactory;
import org.fuelteam.watt.lucky.parallel.IParallel;
import org.fuelteam.watt.lucky.utils.Vardump;
import org.junit.Test;

import com.alibaba.fastjson.JSON;

public class TestParallel {

    IParallel<Context> adds = new IParallel<Context>() {
        @Override
        public void execute(Context context) {
            context.setAdds(100);
            Vardump.print("adds: " + Thread.currentThread());
        }
    };

    IParallel<Context> muls = new IParallel<Context>() {
        @Override
        public void execute(Context context) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            context.setMuls(50);
            Vardump.print("muls: " + Thread.currentThread());
        }
    };

    IParallel<Context> concats = new IParallel<Context>() {
        @Override
        public void execute(Context context) {
            context.setConcats("Hello IParallel");
            Vardump.print("concats: " + Thread.currentThread());
        }
    };

    IParallel<Context> sub1 = new IParallel<Context>() {
        @Override
        public void execute(Context context) {
            Vardump.print("sub1: " + Thread.currentThread() + " | now: " + System.currentTimeMillis());
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            context.getMaps().put(Thread.currentThread().getName(), System.currentTimeMillis());
        }
    };

    IParallel<Context> sub2 = new IParallel<Context>() {
        @Override
        public void execute(Context context) {
            Vardump.print("sub2: " + Thread.currentThread() + " | now: " + System.currentTimeMillis());
            context.getMaps().put(Thread.currentThread().getName(), System.currentTimeMillis());
        }
    };

    @Test
    public void testSync() {
        ForkJoinPool forkJoinPool = new ForkJoinPoolFactory().getObject();

        Context context = new Context();
        DefaultForkJoinParallel<Context> parallel = new DefaultForkJoinParallel<Context>(context);
        parallel.addParallel(adds);
        parallel.addParallel(muls);
        parallel.addParallel(concats);

        DefaultForkJoinParallel<Context> subs = new DefaultForkJoinParallel<Context>(context);
        subs.addParallel(sub1);
        subs.addParallel(sub2);

        parallel.addParallel(subs);

        long start = System.currentTimeMillis();
        // 提交任务, 同步阻塞调用方式
        forkJoinPool.invoke(parallel);
        Vardump.print("costs: " + (System.currentTimeMillis() - start) + " ms");

        Vardump.print("result: " + JSON.toJSONString(context));
    }

    @Test
    public void testAsync() {
        ForkJoinPool forkJoinPool = new ForkJoinPoolFactory().getObject();

        Context context = new Context();
        DefaultForkJoinParallel<Context> parallel = new DefaultForkJoinParallel<Context>(context);
        parallel.addParallel(adds);
        parallel.addParallel(muls);
        parallel.addParallel(concats);
        
        DefaultForkJoinParallel<Context> subs = new DefaultForkJoinParallel<Context>(context);
        subs.addParallel(sub1);
        subs.addParallel(sub2);
        parallel.addParallel(subs);

        long start = System.currentTimeMillis();

        // execute方式异步执行
        forkJoinPool.execute(parallel);

        Vardump.print(String.format("after %s ms, context is: %s", (System.currentTimeMillis() - start), JSON.toJSONString(context)));

        parallel.getContext(); // 等待所有任务执行完毕

        Vardump.print("costs: " + (System.currentTimeMillis() - start) + " ms");

        Vardump.print("result: " + JSON.toJSONString(context));
    }

}
