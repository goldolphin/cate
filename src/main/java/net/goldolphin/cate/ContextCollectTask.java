package net.goldolphin.cate;

/**
 * A task which will complete depending on results of specified tasks.
 * @author goldolphin
 *         2014-09-13 15:43
 */
public class ContextCollectTask<TResult> extends CollectTask<TResult> {
    private final Action1<Context<?, TResult>> action;

    public ContextCollectTask(Action1<Context<?, TResult>> action, ITask<?> ... tasks) {
        super(tasks);
        this.action = action;
    }

    @Override
    protected IContinuation newContinuation(IContinuation cont) {
        return new Continuation(cont, this);
    }

    @Override
    public void onExecute(Object state, IContinuation cont, ITask<?> previous, IScheduler scheduler) {
        Context<?, TResult> context = new Context<Object, TResult>(state, cont, previous, scheduler);
        action.apply(context);
    }
}
