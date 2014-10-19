package net.goldolphin.cate;

/**
 * @author goldolphin
 *         2014-10-02 00:27
 */
public class Action1Task<T> extends Task<Unit> {
    private final Action1<T> action;

    public Action1Task(Action1<T> action) {
        this.action = action;
    }

    @Override
    public IContinuation buildContinuation(IContinuation cont) {
        return new TaskContinuation(cont, this);
    }

    @Override
    public void onExecute(Object state, IContinuation cont, IScheduler scheduler) {
        action.apply((T) state);
        cont.apply(Unit.INSTANCE, IContinuation.END_CONTINUATION, scheduler);
    }
}
