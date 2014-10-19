package net.goldolphin.cate;

/**
 * @author goldolphin
 *         2014-09-07 00:33
 */
public class Func0Task<TResult> extends Task<Unit, TResult> {
    private final Func0<TResult> func;

    public Func0Task(Func0<TResult> func) {
        this.func = func;
    }

    @Override
    public IContinuation buildContinuation(IContinuation cont) {
        return new TaskContinuation<Unit>(cont, this);
    }

    @Override
    public void onExecute(Unit state, IContinuation cont, IScheduler scheduler) {
        cont.apply(func.apply(), IContinuation.END_CONTINUATION, scheduler);
    }
}
