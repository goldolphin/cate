package net.goldolphin.cate;

/**
 * A wrapper task which will execute the wrapped task in the preset scheduler.
 * @author goldolphin
 *         2014-09-26 21:48
 */
public class TaskWithScheduler<TResult> extends Task<TResult> {
    private final ITask<TResult> task;
    private final IScheduler scheduler;

    public TaskWithScheduler(ITask<TResult> task, IScheduler scheduler) {
        this.task = task;
        this.scheduler = scheduler;
    }

    @Override
    public void execute(Object state, IContinuation cont, IScheduler scheduler) {
        task.execute(state, new Continuation(cont, scheduler), this.scheduler);
    }

    @Override
    public void onExecute(Object state, IContinuation cont, ITask<?> previous, IScheduler scheduler) {
        throw new UnsupportedOperationException();
    }

    public static class Continuation implements IContinuation {
        private final IContinuation next;
        private final IScheduler originalScheduler;

        public Continuation(IContinuation next, IScheduler originalScheduler) {
            this.next = next;
            this.originalScheduler = originalScheduler;
        }

        @Override
        public void apply(Object state, ITask<?> previous, IScheduler scheduler) {
            next.apply(state, previous, originalScheduler);
        }
    }
}
