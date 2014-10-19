package net.goldolphin.cate;

/**
 * @author goldolphin
 *         2014-09-08 21:47
 */
public class ContextTask<T, TResult> extends Task<TResult> {
    private final ContextAction<T, TResult> action;

    public ContextTask(ContextAction<T, TResult> action) {
        this.action = action;
    }

    @Override
    public IContinuation buildContinuation(IContinuation cont) {
        return new TaskContinuation(cont, this);
    }

    @Override
    public void onExecute(Object state, final IContinuation cont, final IScheduler scheduler) {
        Context<T, TResult> context = new Context<T, TResult>((T) state, cont, scheduler);
        action.apply(context);
    }
}
