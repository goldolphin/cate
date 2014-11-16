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

    /**
     * For numbering the result of a task with the index of this task.
     */
    public static class IndexContinuation implements IContinuation {
        private final int index;
        private final IContinuation next;

        public IndexContinuation(int index, IContinuation next) {
            this.index = index;
            this.next = next;
        }

        @Override
        public void apply(Object state, Environment environment, IScheduler scheduler) {
            next.apply(new Result(index, state), environment, scheduler);
        }
    }

    /**
     * Counter for recording results of the tasks.
     */
    public static class Counter {
        private final Object[] results;
        private int complete;

        public Counter(int total) {
            results = new Object[total];
            complete = 0;
        }

        public void record(Result result) {
            int total = results.length;
            complete += 1;
            if (complete > total) {
                throw new IllegalStateException("Invalid complete value: " + complete + " exceeds " + total);
            }
            results[result.index] = result.value;
        }

        public Object[] getResults() {
            return results;
        }

        public int getComplete() {
            return complete;
        }
    }
}
