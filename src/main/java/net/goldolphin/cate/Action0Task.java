package net.goldolphin.cate;

/**
 * @author goldolphin
 *         2014-10-02 00:27
 */
public class Action0Task extends Task<Unit> {
    private final Action0 action;

    public Action0Task(Action0 action) {
        this.action = action;
    }

    @Override
    public void execute(Object state, IContinuation cont, IScheduler scheduler) {
        action.apply();
        cont.apply(Unit.INSTANCE, this, scheduler);
    }

    @Override
    public void onExecute(Object state, IContinuation cont, ITask<?> previous, IScheduler scheduler) {
        scheduler.schedule(this, state, cont, null);
    }
}
