package net.goldolphin.cate;

/**
 * A task which will complete when any specified task complete.
 * @author goldolphin
 *         2014-09-06 21:27
 */
public class WhenAnyTask extends CollectTask<Object> {

    public WhenAnyTask(ITask<?> ... tasks) {
        super(tasks);
    }

    @Override
    protected IContinuation buildCollectorContinuation(IContinuation cont) {
        return new Continuation(cont, this);
    }

    @Override
    public void onExecute(Object state, IContinuation cont, IScheduler scheduler) {
        throw new UnsupportedOperationException();
    }

    public static class Continuation implements IContinuation {
        private final IContinuation next;
        private final WhenAnyTask task;
        private int complete = 0;

        public Continuation(IContinuation next, WhenAnyTask task) {
            this.next = next;
            this.task = task;
        }

        @Override
        public void apply(Object state, IScheduler scheduler) {
            complete += 1;
            int total = task.getTasks().length;
            if (complete > total) {
                throw new IllegalStateException("Invalid complete value: " + complete + " exceeds " + total);
            } else if (complete == 1) {
                next.apply(state, scheduler);
            }
        }
    }
}
