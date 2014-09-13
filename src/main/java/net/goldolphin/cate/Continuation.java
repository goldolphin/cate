package net.goldolphin.cate;

/**
 * A basic continuation.
 * @author goldolphin
 *         2014-09-12 22:10
 */
public class Continuation implements IContinuation {
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
