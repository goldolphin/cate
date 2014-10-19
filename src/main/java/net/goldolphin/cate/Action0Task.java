package net.goldolphin.cate;

/**
 * @author goldolphin
 *         2014-10-02 00:27
 */
public class Action0Task extends Task<Unit, Unit> {
    private final Action0 action;

    public Action0Task(Action0 action) {
        this.action = action;
    }

    @Override
    public IContinuation buildContinuation(IContinuation cont) {
        return new TaskContinuation<Unit>(cont, this);
    }

    @Override
    public void onExecute(Unit state, IContinuation cont, IScheduler scheduler) {
        action.apply();
        cont.apply(Unit.VALUE, IContinuation.END_CONTINUATION, scheduler);
    }
}
