package net.goldolphin.cate;

/**
 * @author goldolphin
 *         2014-09-08 12:03
 */
public class FlattenTask<TInput, TResult, TTask extends ITask<Unit, TResult>> extends Task<TInput, TResult> {
    private final ITask<TInput, TTask> task;

    public FlattenTask(ITask<TInput, TTask> task) {
        this.task = task;
    }

    @Override
    public IContinuation buildContinuation(IContinuation cont) {
        return task.buildContinuation(new Continuation(cont));
    }

    @Override
    public void onExecute(TInput state, IContinuation cont, IScheduler scheduler) {
        throw new UnsupportedOperationException();
    }

    public static class Continuation implements IContinuation {
        private final IContinuation next;

        public Continuation(IContinuation next) {
            this.next = next;
        }

        @Override
        public void apply(Object state, IContinuation subCont, IScheduler scheduler) {
            ((ITask<Unit, ?>) state).buildContinuation(next).apply(Unit.VALUE, subCont, scheduler);
        }
    }
}
