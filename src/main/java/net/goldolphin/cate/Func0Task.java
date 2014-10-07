package net.goldolphin.cate;

/**
 * @author goldolphin
 *         2014-09-07 00:33
 */
public class Func0Task<TResult> extends Task<TResult> {
    private final Func0<TResult> func;

    public Func0Task(Func0<TResult> func) {
        this.func = func;
    }

    @Override
    public IContinuation buildContinuation(IContinuation cont) {
        return new Continuation(cont, this);
    }

    @Override
    public void onExecute(Object state, IContinuation cont, IScheduler scheduler) {
        cont.apply(func.apply(), scheduler);
    }
}
