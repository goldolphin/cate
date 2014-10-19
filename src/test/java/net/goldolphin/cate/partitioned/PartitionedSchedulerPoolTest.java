package net.goldolphin.cate.partitioned;

import net.goldolphin.cate.Context;
import net.goldolphin.cate.ContextAction;
import net.goldolphin.cate.IScheduler;
import net.goldolphin.cate.SynchronizedScheduler;
import net.goldolphin.cate.Task;
import net.goldolphin.cate.Waiter;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class PartitionedSchedulerPoolTest {
    @Test
    public void testScheduler() throws Exception {
        int partitionNum = 10;
        IScheduler[] schedulers = new IScheduler[partitionNum];
        for (int i = 0; i < partitionNum; i ++) {
            schedulers[i] = new SynchronizedScheduler();
        }
        PartitionedSchedulerPool<Integer> schedulerPool
                = new PartitionedSchedulerPool<Integer>(schedulers, HashedPartitioner.<Integer>instance());
        final PartitionedStore<Integer, IScheduler> store = new PartitionedStore<Integer, IScheduler>(schedulerPool);
        Task<Integer, Boolean> task = Task.create(new ContextAction<Integer, Boolean>() {
            @Override
            public void apply(Context<Integer, Boolean> context) {
                Integer key = context.getState();
                IScheduler scheduler = store.get(key);
                if (scheduler == null) {
                    scheduler = context.getScheduler();
                    store.put(key, scheduler);
                } else {
                    System.out.format("%d. stored scheduler: %s, current scheduler: %s\n", key, scheduler, context.getScheduler());
                }
                context.resume(scheduler == context.getScheduler());
            }
        });

        for (int i = 0; i < 100; i ++) {
            Waiter<Integer, Boolean> waiter = task.continueWithWaiter();
            waiter.execute(i, schedulerPool.getScheduler(i));
            Assert.assertTrue(waiter.getResult());
        }

        for (int i = 0; i < 10; i ++) {
            Waiter<Integer, Boolean> waiter = task.continueWithWaiter();
            waiter.execute(i, schedulerPool.getScheduler(i));
            Assert.assertTrue(waiter.getResult());
        }

        for (Map<Integer, IScheduler> map: store.getData()) {
            IScheduler s0 = null;
            for (IScheduler s: map.values()) {
                if (s0 == null) {
                    s0 = s;
                } else {
                    Assert.assertSame(s0, s);
                }
            }
        }
    }
}
