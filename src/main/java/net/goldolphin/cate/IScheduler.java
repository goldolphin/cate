package net.goldolphin.cate;

/**
 * A scheduler.
 * @author goldolphin
 *         2014-09-05 22:46
 */
public interface IScheduler {
    /**
     * Schedule and execute a task itself, i.e. invoke {@link ITask#onExecute}.
     * @param task
     * @param state
     * @param cont
     * @param previous
     */
    public void schedule(ITask<?> task, Object state, IContinuation cont, ITask<?> previous);
}
