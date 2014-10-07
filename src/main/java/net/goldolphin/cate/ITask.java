package net.goldolphin.cate;

/**
 * A task.
 * @param <TResult> the result type.
 * @author goldolphin
 *         2014-09-05 22:46
 */
public interface ITask<TResult> {
    /**
     * Build the continuation of this task.
     * @param cont current continuation.
     * @return the continuation of this task.
     */
    public IContinuation buildContinuation(IContinuation cont);

    /**
     * Action should be done when the task is executed. Continuation should be applied usually.
     * @param state input state.
     * @param cont the continuation
     * @param scheduler the scheduler.
     */
    public void onExecute(Object state, IContinuation cont, IScheduler scheduler);
}