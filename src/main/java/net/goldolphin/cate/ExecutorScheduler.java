package net.goldolphin.cate;

import java.util.concurrent.Executor;

/**
 * Scheduler based on {@link java.util.concurrent.Executor}
 * @author goldolphin
 *         2014-09-05 22:52
 */
public class ExecutorScheduler extends SynchronizedScheduler {
    private final Executor executor;

    public ExecutorScheduler(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void schedule(final ITask<?> task, final Object state, final IContinuation cont, final ITask<?> previous) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ExecutorScheduler.super.schedule(task, state, cont, previous);
            }
        });
    }
}
