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
    public void onExecute(Object state, IContinuation cont, ITask<?> previous, IScheduler scheduler) {
        cont.apply(func.apply(), this, scheduler);
    }

    @Override
    public void execute(Object state, IContinuation cont, IScheduler scheduler) {
        scheduler.schedule(this, state, cont, null);
    }
}
