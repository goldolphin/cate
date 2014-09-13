package net.goldolphin.cate;

/**
 * A task which will complete when all specified tasks complete.
 * @author goldolphin
 *         2014-09-06 18:27
 */
public class WhenAllTask extends CollectTask<Object[]> {

    public WhenAllTask(ITask<?> ... tasks) {
        super(tasks);
    }

    @Override
    protected IContinuation newContinuation(IContinuation cont) {
        return new Continuation(cont, this);
    }

    public static class Continuation implements IContinuation {
        private final IContinuation next;
        private final WhenAllTask task;
        private final Object[] results;
        private int complete = 0;

        public Continuation(IContinuation next, WhenAllTask task) {
            this.next = next;
            this.task = task;
            results = new Object[task.getTasks().length];
        }

        @Override
        public void apply(Object state, ITask<?> previous, IScheduler scheduler) {
            complete += 1;
            int total = task.getTasks().length;
            if (complete > total) {
                throw new IllegalStateException("Invalid complete value: " + complete + " exceeds " + total);
            }
            setResult(state, previous);
            if (complete == total) {
                next.apply(results, previous, scheduler);
            }
        }

        private void setResult(Object state, ITask<?> task) {
            for (int i = 0; i < results.length; i ++) {
                if (task == this.task.getTasks()[i]) {
                    results[i] = state;
                    break;
                }
            }
        }
    }
}
