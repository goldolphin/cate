package net.goldolphin.cate;

/**
 * @author goldolphin
 *         2014-09-06 15:38
 */
public class SeqTask<AInput, AResult, TResult> extends Task<AInput, TResult> {
    protected final ITask<AInput, AResult> antecedent;
    protected final ITask<AResult, TResult> subsequent;

    public SeqTask(ITask<AInput, AResult> antecedent, ITask<AResult, TResult> subsequent) {
        this.antecedent = antecedent;
        this.subsequent = subsequent;
    }

    @Override
    public IContinuation buildContinuation(IContinuation cont) {
        return antecedent.buildContinuation(subsequent.buildContinuation(cont));
    }

    @Override
    public void onExecute(AInput state, IContinuation cont, IScheduler scheduler) {
        throw new UnsupportedOperationException();
    }
}
