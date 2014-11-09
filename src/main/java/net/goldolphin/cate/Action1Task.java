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
    public IContinuation buildContinuation(final IContinuation cont) {
        return new IContinuation() {
            @Override
            public void apply(Object state, IContinuation subCont, IScheduler scheduler) {
                action.apply((TInput) state);
                cont.apply(Unit.VALUE, subCont, scheduler);
            }
        };
    }
}
