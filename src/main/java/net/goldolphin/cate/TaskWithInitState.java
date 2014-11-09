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
        final IContinuation newCont = task.buildContinuation(cont);
        return new IContinuation() {
            @Override
            public void apply(Object state, IContinuation subCont, IScheduler scheduler) {
                newCont.apply(initState, subCont, scheduler);
            }
        };
    }
}
