package net.goldolphin.cate;

/**
 * Scheduler that execute tasks in current thread.
 * @author goldolphin
 *         2014-09-06 15:10
 */
public class SynchronizedScheduler implements IScheduler {
    @Override
    public void schedule(Object state, IContinuation cont, ITask<?> previous) {
        cont.apply(state, previous, this);
    }
}
