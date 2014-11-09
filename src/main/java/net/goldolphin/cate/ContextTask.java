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
    public IContinuation buildContinuation(final IContinuation cont) {
        return new IContinuation() {
            @Override
            public void apply(Object state, IContinuation subCont, IScheduler scheduler) {
                Context<TInput, TResult> context = new Context<TInput, TResult>((TInput) state, cont, subCont, scheduler);
                action.apply(context);
            }
        };
    }
}
