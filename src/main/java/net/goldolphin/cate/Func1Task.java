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
    public IContinuation buildContinuation(final IContinuation cont) {
        return new IContinuation() {
            @Override
            public void apply(Object state, Environment environment, IScheduler scheduler) {
                cont.apply(func.apply((TInput) state), environment, scheduler);
            }
        };
    }
}
