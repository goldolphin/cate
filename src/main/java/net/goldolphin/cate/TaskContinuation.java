package net.goldolphin.cate;

/**
 * A basic continuation for tasks.
 * @author goldolphin
 *         2014-10-02 12:00
 */
public class TaskContinuation implements IContinuation {
    protected final IContinuation next;
    protected final ITask<?> task;

    /**
     * Constructor.
     * @param next
     * @param task
     */
    public TaskContinuation(IContinuation next, ITask<?> task) {
        this.next = next;
        this.task = task;
    }

    @Override
    public void apply(Object state, IContinuation subCont, IScheduler scheduler) {
        task.onExecute(state, SeqContinuation.seq(next, subCont), scheduler);
    }

    @Override
    public String toString() {
        return "Continuation{" +
                "next=" + next +
                ", task=" + task +
                '}';
    }
}
