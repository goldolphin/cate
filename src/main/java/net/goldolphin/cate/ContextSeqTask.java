package net.goldolphin.cate;

/**
 * @author caofuxiang
 *         2014-09-11 10:27
 */
public class ContextSeqTask<AResult, TResult> extends SeqTask<AResult, TResult> {
    private final Action1<Context<AResult, TResult>> action;

    public ContextSeqTask(ITask<AResult> antecedent, Action1<Context<AResult, TResult>> action, boolean flatten) {
        super(antecedent, flatten);
        this.action = action;
    }

    @Override
    public void onExecute(Object state, final IContinuation cont, final ITask<?> previous, final IScheduler scheduler) {
        Context<AResult, TResult> context = new Context<AResult, TResult>((AResult) state, cont, this, scheduler);
        action.apply(context);
    }

    @Override
    protected TResult evaluate(Object value) {
        throw new UnsupportedOperationException();
    }
}
