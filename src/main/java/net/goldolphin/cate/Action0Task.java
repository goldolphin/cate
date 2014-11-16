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
    public IContinuation buildContinuation(final IContinuation cont) {
        return new IContinuation() {
            @Override
            public void apply(Object state, Environment environment, IScheduler scheduler) {
                action.apply();
                cont.apply(Unit.VALUE, environment, scheduler);
            }
        };
    }
}
