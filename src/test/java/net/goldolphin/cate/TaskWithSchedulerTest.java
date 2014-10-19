package net.goldolphin.cate;

import org.junit.Assert;
import org.junit.Test;

public class TaskWithSchedulerTest {
    @Test
    public void testTaskWithScheduler() throws Exception {
        Waiter<Unit, IScheduler> waiter1 = fooAsync().continueWithWaiter();
        waiter1.execute(scheduler1);
        Assert.assertEquals(scheduler1, waiter1.getResult());

        Waiter<Unit, IScheduler> waiter2 = fooAsync().withScheduler(scheduler2).continueWithWaiter();
        waiter2.execute(scheduler1);
        Assert.assertEquals(scheduler2, waiter2.getResult());
    }

    private IScheduler scheduler1 = new SynchronizedScheduler();
    private IScheduler scheduler2 = new SynchronizedScheduler();

    Task<Unit, IScheduler> fooAsync() {
        return Task.create(new ContextAction<Unit, IScheduler>() {
            @Override
            public void apply(Context<Unit, IScheduler> context) {
                context.resume(context.getScheduler());
            }
        });
    }

}