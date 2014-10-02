package net.goldolphin.cate;

/**
 * @author goldolphin
 *         2014-09-06 15:38
 */
public abstract class SeqTask<AResult, TResult> extends Task<TResult> {
    protected final ITask<AResult> antecedent;
    private final boolean flatten;

    public SeqTask(ITask<AResult> antecedent, boolean flatten) {
        this.antecedent = antecedent;
        this.flatten = flatten;
    }

    @Override
    public void execute(Object state, IContinuation cont, IScheduler scheduler) {
        antecedent.execute(state,
                flatten ? new FlattenContinuation(cont, this) : new Continuation(cont, this),
                scheduler);
    }

    @Override
    public void onExecute(Object state, IContinuation cont, ITask<?> previous, IScheduler scheduler) {
        cont.apply(evaluate(state), this, scheduler);
    }

    /**
     * Evaluate the task, and the returned value will be set as the result.
     * @return
     */
    protected abstract TResult evaluate(Object value);

    public static class Continuation implements IContinuation {
        protected final IContinuation next;
        protected final ITask<?> task;

        public Continuation(IContinuation next, ITask<?> task) {
            this.next = next;
            this.task = task;
        }

        @Override
        public void apply(Object state, ITask<?> previous, IScheduler scheduler) {
            task.onExecute(state, next, previous, scheduler);
        }
    }

    public static class FlattenContinuation extends Continuation {
        public FlattenContinuation(IContinuation next, ITask<?> task) {
            super(next, task);
        }

        @Override
        public void apply(Object state, ITask<?> previous, IScheduler scheduler) {
            if (state instanceof ITask<?>) {
                ((ITask<?>) state).execute(null, this, scheduler);
            } else {
                super.apply(state, previous, scheduler);
            }
        }
    }
}
