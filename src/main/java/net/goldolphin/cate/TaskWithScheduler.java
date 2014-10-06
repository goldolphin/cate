package net.goldolphin.cate;

/**
 * A wrapper task which will execute the wrapped task in specified scheduler.
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
    public IContinuation buildContinuation(IContinuation cont) {
        return new net.goldolphin.cate.Continuation(cont, this);
    }

    @Override
    public void onExecute(Object state, IContinuation cont, ITask<?> previous, IScheduler scheduler) {
        IContinuation newCont = task.buildContinuation(new Continuation(cont, scheduler));
        this.scheduler.schedule(state, newCont, this);
    }

    public static class Continuation implements IContinuation {
        private final IContinuation next;
        private final IScheduler scheduler;

        public Continuation(IContinuation next, IScheduler scheduler) {
            this.next = next;
            this.scheduler = scheduler;
        }

        @Override
        public void apply(Object state, ITask<?> previous, IScheduler scheduler) {
            this.scheduler.schedule(state, next, previous);
        }
    }
}
