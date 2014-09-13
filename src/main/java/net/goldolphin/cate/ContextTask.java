package net.goldolphin.cate;

/**
 * @author goldolphin
 *         2014-09-08 21:47
 */
public class ContextTask<T, TResult> extends Task<TResult> {
    private final Action1<Context<T, TResult>> action;

    public ContextTask(Action1<Context<T, TResult>> action) {
        this.action = action;
    }

    @Override
    public void execute(Object state, IContinuation cont, IScheduler scheduler) {
        scheduler.schedule(this, state, cont, null);
    }

    @Override
    public void onExecute(Object state, final IContinuation cont, final ITask<?> previous, final IScheduler scheduler) {
        Context<T, TResult> context = new Context<T, TResult>((T) state, cont, this, scheduler);
        action.apply(context);
    }
}
