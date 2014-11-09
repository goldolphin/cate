package net.goldolphin.cate;

/**
 * Interface of a task.<p />
 * A task has an input(called input state) & and a result(called output state).<p />
 * In short, the input state is passed from the antecedent task or by the user, and the output state will be
 * passed to the subsequent task(s) through the continuation. Users can change the default behavior by implementing
 * customized tasks & continuations.
 *
 * @param <TInput> the input type.
 * @param <TResult> the result type.
 * @author goldolphin
 *         2014-09-05 22:46
 */
public interface ITask<TInput, TResult> {
    /**
     * Build the continuation of this task.
     * @param cont current continuation.
     * @return the continuation of this task.
     */
    public IContinuation buildContinuation(IContinuation cont);
}
