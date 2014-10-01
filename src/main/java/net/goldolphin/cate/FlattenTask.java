package net.goldolphin.cate;

/**
 * @author goldolphin
 *         2014-09-08 12:03
 */
public class FlattenTask<TResult, TTask extends ITask<TResult>> extends SeqTask<TTask, TResult> {

    public FlattenTask(ITask<TTask> antecedent) {
        super(antecedent, true);
    }

    @Override
    public void execute(Object state, IContinuation cont, IScheduler scheduler) {
        antecedent.execute(state, new Continuation(cont, this), scheduler);
    }

    @Override
    protected TResult evaluate(Object value) {
        return (TResult) value;
    }

    public static class Continuation implements IContinuation {
        private final IContinuation next;

        public Continuation(IContinuation next, ITask<?> task) {
            this.next = new SeqTask.Continuation(next, task);
        }

        @Override
        public void apply(Object state, ITask<?> previous, IScheduler scheduler) {
            ((ITask<?>) state).execute(null, next, scheduler);
        }
    }
}
