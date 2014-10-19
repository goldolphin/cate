package net.goldolphin.cate;

/**
 * @author goldolphin
 *         2014-09-08 21:47
 */
public class ContextTask<TInput, TResult> extends Task<TInput, TResult> {
    private final ContextAction<TInput, TResult> action;

    public ContextTask(ContextAction<TInput, TResult> action) {
        this.action = action;
    }

    @Override
    public IContinuation buildContinuation(IContinuation cont) {
        return new TaskContinuation<TInput>(cont, this);
    }

    @Override
    public void onExecute(TInput state, final IContinuation cont, final IScheduler scheduler) {
        Context<TInput, TResult> context = new Context<TInput, TResult>(state, cont, scheduler);
        action.apply(context);
    }
}
