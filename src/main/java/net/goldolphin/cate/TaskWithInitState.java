package net.goldolphin.cate;

/**
 * @author caofuxiang
 *         2014-09-11 11:40
 */
public class TaskWithInitState<T, TResult> extends Task<TResult> {
    private final ITask<?> task;
    private final T initState;

    public TaskWithInitState(ITask<?> task, T initState) {
        this.task = task;
        this.initState = initState;
    }

    @Override
    public IContinuation buildContinuation(IContinuation cont) {
        return new Continuation(task.buildContinuation(cont), this);
    }

    @Override
    public void onExecute(Object state, IContinuation cont, ITask<?> previous, IScheduler scheduler) {
        cont.apply(initState, this, scheduler);
    }
}
