package net.goldolphin.cate;

/**
 * An abstract task which will complete depending on results of specified tasks.
 * @author goldolphin
 *         2014-09-13 15:43
 */
public abstract class CollectTask<TResult> extends Task<TResult> {
    private final ITask<?>[] tasks;

    public CollectTask(ITask<?>[] tasks) {
        this.tasks = tasks;
    }

    /**
     * Get tasks to wait.
     *
     * @return
     */
    public ITask<?>[] getTasks() {
        return tasks;
    }

    @Override
    public void execute(Object state, IContinuation cont, IScheduler scheduler) {
        IContinuation newCont = newContinuation(cont);
        for (ITask<?> task : tasks) {
            task.execute(state, newCont, scheduler);
        }
    }

    /**
     * Build a new continuation.
     *
     * @return
     */
    protected IContinuation newContinuation(IContinuation cont) {
        return new Continuation(cont, this);
    }

    public static class Continuation implements IContinuation {
        protected final IContinuation next;
        protected final ITask<?> task;

        public Continuation(IContinuation next, ITask<?> task) {
            this.next = next;
            this.task = task;
        }

        @Override
        public void apply(Object state, ITask<?> previous, IScheduler scheduler) {
            scheduler.schedule(task, state, next, previous);
        }
    }
}