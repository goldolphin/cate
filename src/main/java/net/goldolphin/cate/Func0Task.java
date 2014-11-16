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
    public IContinuation buildContinuation(final IContinuation cont) {
        return new IContinuation() {
            @Override
            public void apply(Object state, Environment environment, IScheduler scheduler) {
                cont.apply(func.apply(), environment, scheduler);
            }
        };
    }
}
