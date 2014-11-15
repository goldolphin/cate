package net.goldolphin.cate;

/**
 * An abstract task which will complete depending on results of specified tasks.
 * @author goldolphin
 *         2014-09-13 15:43
 */
public abstract class CollectTask<TInput, TResult> extends Task<TInput, TResult> {
    protected final ITask<TInput, ?>[] tasks;

    public CollectTask(ITask<TInput, ?>[] tasks) {
        this.tasks = tasks;
    }

    /**
     * Returns tasks to wait.
     *
     * @return
     */
    public ITask<TInput, ?>[] getTasks() {
        return tasks;
    }

    public static class IndexContinuation implements IContinuation {
        private final int index;
        private final IContinuation next;

        public IndexContinuation(int index, IContinuation next) {
            this.index = index;
            this.next = next;
        }

        @Override
        public void apply(Object state, IContinuation subCont, IScheduler scheduler) {
            next.apply(new Result(index, state), subCont, scheduler);
        }
    }

    /**
     * The result of one of the tasks to collect with the index of this task.
     */
    public static class Result {
        public final int index;
        public final Object value;

        public Result(int index, Object value) {
            this.index = index;
            this.value = value;
        }
    }
}
