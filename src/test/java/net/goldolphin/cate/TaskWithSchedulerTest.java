package net.goldolphin.cate;

import org.junit.Assert;
import org.junit.Test;

public class TaskWithSchedulerTest {
    @Test
    public void testWithScheduler() throws Exception {
        Waiter<IScheduler> waiter1 = fooAsync().continueWithWaiter();
        waiter1.execute(scheduler1);
        Assert.assertEquals(scheduler1, waiter1.getResult());

        Waiter<IScheduler> waiter2 = fooAsync().withScheduler(scheduler2).continueWithWaiter();
        waiter2.execute(scheduler1);
        Assert.assertEquals(scheduler2, waiter2.getResult());
    }

    private IScheduler scheduler1 = new SynchronizedScheduler();
    private IScheduler scheduler2 = new SynchronizedScheduler();

    Task<IScheduler> fooAsync() {
        return Task.create(new Action1<Context<Object, IScheduler>>() {
            @Override
            public void apply(Context<Object, IScheduler> context) {
                context.resume(context.getScheduler());
            }
        });
    }

}