package net.goldolphin.cate;

/**
 * @author goldolphin
 *         2014-09-06 15:38
 */
public class SeqTask<AResult, TResult> extends Task<TResult> {
    protected final ITask<AResult> antecedent;
    protected final ITask<TResult> subsequent;

    public SeqTask(ITask<AResult> antecedent, ITask<TResult> subsequent) {
        this.antecedent = antecedent;
        this.subsequent = subsequent;
    }

    @Override
    public IContinuation buildContinuation(IContinuation cont) {
        return antecedent.buildContinuation(subsequent.buildContinuation(cont));
    }

    @Override
    public void onExecute(Object state, IContinuation cont, ITask<?> previous, IScheduler scheduler) {
        throw new UnsupportedOperationException();
    }
}
