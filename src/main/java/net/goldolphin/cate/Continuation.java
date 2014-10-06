package net.goldolphin.cate;

/**
 * A basic continuation.
 * @author goldolphin
 *         2014-10-02 12:00
 */
public class Continuation implements IContinuation {
    protected final IContinuation next;
    protected final ITask<?> task;

    /**
     * Constructor.
     * @param next
     * @param task
     */
    public Continuation(IContinuation next, ITask<?> task) {
        this.next = next;
        this.task = task;
    }

    @Override
    public void apply(Object state, ITask<?> previous, IScheduler scheduler) {
        task.onExecute(state, next, previous, scheduler);
    }

    @Override
    public String toString() {
        return "Continuation{" +
                "next=" + next +
                ", task=" + task +
                '}';
    }
}
