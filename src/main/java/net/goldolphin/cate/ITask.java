package net.goldolphin.cate;

/**
 * Interface of a task.<p />
 * A task has an input(called input state) & and a result(called output state).<p />
 * In short, the input state is passed from the antecedent task or by the user, and the output state will be
 * passed to the subsequent task(s) through the continuation. Users can change the default behavior by implementing
 * customized tasks & continuations.
 *
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
     * Action should be taken when the task is executed. Continuation should be applied usually.
     * @param state input state.
     * @param cont the continuation
     * @param scheduler the scheduler.
     */
    public void onExecute(Object state, IContinuation cont, IScheduler scheduler);
}