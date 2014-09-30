package net.goldolphin.cate.partitioned;

import net.goldolphin.cate.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class PartitionedSchedulerTest {
    @Test
    public void testScheduler() throws Exception {
        int partitionNum = 10;
        IScheduler[] schedulers = new IScheduler[partitionNum];
        for (int i = 0; i < partitionNum; i ++) {
            schedulers[i] = new SynchronizedScheduler();
        }
        PartitionedScheduler scheduler = new PartitionedScheduler(schedulers, new IdentityKeyExtractor(), new HashedPartitioner());
        final PartitionedStore<Integer, IScheduler> store = new PartitionedStore<Integer, IScheduler>(scheduler);
        Task<Boolean> task = Task.create(new Action1<Context<Integer, Boolean>>() {
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
            Waiter<Boolean> waiter = task.continueWithWaiter();
            waiter.execute(i, scheduler);
            Assert.assertTrue(waiter.getResult());
        }

        for (int i = 0; i < 10; i ++) {
            Waiter<Boolean> waiter = task.continueWithWaiter();
            waiter.execute(i, scheduler);
            Assert.assertTrue(waiter.getResult());
        }

        for (Map<Integer, IScheduler> map: store.getMaps()) {
            System.out.println(map);
            IScheduler s0 = null;
            for (IScheduler s: map.values()) {
                if (s0 == null) {
                    s0 = s;
                } else {
                    Assert.assertEquals(s0, s);
                }
            }
        }
    }
}