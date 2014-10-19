package net.goldolphin.cate;

/**
 * @author goldolphin
 *         2014-09-08 12:03
 */
public class FlattenTask<TResult, TTask extends ITask<TResult>> extends Task<TResult> {
    private final ITask<TTask> task;

    public FlattenTask(ITask<TTask> task) {
        this.task = task;
    }

    @Override
    public IContinuation buildContinuation(IContinuation cont) {
        return task.buildContinuation(new Continuation(cont));
    }

    @Override
    public void onExecute(Object state, IContinuation cont, IScheduler scheduler) {
        throw new UnsupportedOperationException();
    }

    public static class Continuation implements IContinuation {
        private final IContinuation next;

        public Continuation(IContinuation next) {
            this.next = next;
        }

        @Override
        public void apply(Object state, IContinuation subCont, IScheduler scheduler) {
            ((ITask<?>) state).buildContinuation(next).apply(null, subCont, scheduler);
        }
    }
}
