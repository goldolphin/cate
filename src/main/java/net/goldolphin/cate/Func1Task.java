package net.goldolphin.cate;

/**
 * @author caofuxiang
 *         2014-09-10 10:43
 */
public class Func1Task<T, TResult> extends Task<TResult> {
    private final Func1<T, TResult> func;

    public Func1Task(Func1<T, TResult> func) {
        this.func = func;
    }

    @Override
    public IContinuation buildContinuation(IContinuation cont) {
        return new TaskContinuation(cont, this);
    }

    @Override
    public void onExecute(Object state, IContinuation cont, IScheduler scheduler) {
        cont.apply(func.apply((T) state), IContinuation.END_CONTINUATION, scheduler);
    }
}
