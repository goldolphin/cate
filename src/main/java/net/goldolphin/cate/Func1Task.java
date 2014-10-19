package net.goldolphin.cate;

/**
 * @author caofuxiang
 *         2014-09-10 10:43
 */
public class Func1Task<TInput, TResult> extends Task<TInput, TResult> {
    private final Func1<TInput, TResult> func;

    public Func1Task(Func1<TInput, TResult> func) {
        this.func = func;
    }

    @Override
    public IContinuation buildContinuation(IContinuation cont) {
        return new TaskContinuation<TInput>(cont, this);
    }

    @Override
    public void onExecute(TInput state, IContinuation cont, IScheduler scheduler) {
        cont.apply(func.apply(state), IContinuation.END_CONTINUATION, scheduler);
    }
}
