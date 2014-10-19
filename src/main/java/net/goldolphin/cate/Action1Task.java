package net.goldolphin.cate;

/**
 * @author goldolphin
 *         2014-10-02 00:27
 */
public class Action1Task<TInput> extends Task<TInput, Unit> {
    private final Action1<TInput> action;

    public Action1Task(Action1<TInput> action) {
        this.action = action;
    }

    @Override
    public IContinuation buildContinuation(IContinuation cont) {
        return new TaskContinuation<TInput>(cont, this);
    }

    @Override
    public void onExecute(Object state, IContinuation cont, IScheduler scheduler) {
        action.apply((TInput) state);
        cont.apply(Unit.VALUE, IContinuation.END_CONTINUATION, scheduler);
    }
}
