package net.goldolphin.cate;

/**
 * A task.
 * @param <TResult> result type.
 * @author goldolphin
 *         2014-09-05 22:46
 */
public interface ITask<TResult> {
    /**
     * Execute the task with specified init state & continuation.
     * @param state the init state of the task.
     * @param cont
     * @param scheduler
     */
    public void execute(Object state, IContinuation cont, IScheduler scheduler);

    /**
     * Action should be done when the task is executed. Continuation should be applied usually.
     * @param state
     * @param cont
     * @param previous
     * @param scheduler
     */
    public void onExecute(Object state, IContinuation cont, ITask<?> previous, IScheduler scheduler);
}