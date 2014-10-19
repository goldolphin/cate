package net.goldolphin.cate;

/**
 * @author caofuxiang
 *         2014-09-11 11:40
 */
public class TaskWithInitState<T, TResult> extends Task<Unit, TResult> {
    private final ITask<T, TResult> task;
    private final T initState;

    public TaskWithInitState(ITask<T, TResult> task, T initState) {
        this.task = task;
        this.initState = initState;
    }

    @Override
    public IContinuation buildContinuation(IContinuation cont) {
        return new TaskContinuation<Unit>(task.buildContinuation(cont), this);
    }

    @Override
    public void onExecute(Unit state, IContinuation cont, IScheduler scheduler) {
        cont.apply(initState, IContinuation.END_CONTINUATION, scheduler);
    }
}
