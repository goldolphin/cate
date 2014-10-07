package net.goldolphin.cate.util;

import net.goldolphin.cate.Context;
import net.goldolphin.cate.ContextAction;
import net.goldolphin.cate.Task;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A timer based on {@link ScheduledExecutorService}.
 * @author goldolphin
 *         2014-10-07 11:41
 */
public class ExecutorTimer extends Timer {
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public void shutdown() {
        executor.shutdown();
    }

    @Override
    public <T> Task<T> delay(final T result, final long delay, final TimeUnit unit) {
        return Task.create(new ContextAction<Object, T>() {
            @Override
            public void apply(final Context<Object, T> context) {
                executor.schedule(new Runnable() {
                    @Override
                    public void run() {
                        context.resume(result);
                    }
                }, delay, unit);
            }
        });
    }
}
