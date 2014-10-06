package net.goldolphin.cate;

/**
 * A scheduler.
 * @author goldolphin
 *         2014-09-05 22:46
 */
public interface IScheduler {
    /**
     * Schedule and execute a task.
     * @param task
     * @param state
     * @param cont
     */
    public void schedule(Object state, IContinuation cont, ITask<?> previous);
}
