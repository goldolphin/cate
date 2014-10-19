package net.goldolphin.cate;

/**
 * A task to wait the result of its antecedent.
 * @param <TResult> result type.
 * @author goldolphin
 *         2014-09-06 18:05
 */
public class Waiter<TResult> extends Task<TResult> {
    private final ITask<TResult> task;
    private final Object lock = new Object();
    private volatile boolean isComplete = false;
    private volatile TResult result;

    public Waiter(ITask<TResult> task) {
        this.task = task;
    }

    /**
     * Whether the task is complete.
     * @return
     */
    public boolean isComplete() {
        return isComplete;
    }

    /**
     * Set the result of this task.
     * @param result
     */
    public void setResult(TResult result) {
        synchronized (lock) {
            if (isComplete) {
                throw new IllegalStateException("The task is already completed.");
            }
            this.result = result;
            isComplete = true;
            lock.notifyAll();
        }
    }

    /**
     * Get the result in a blocking way.
     * @return
     */
    public TResult getResult() {
        synchronized (lock) {
            while (!isComplete) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            return result;
        }
    }

    @Override
    public IContinuation buildContinuation(IContinuation cont) {
        return task.buildContinuation(new TaskContinuation(cont, this));
    }

    @Override
    public void onExecute(Object state, IContinuation cont, IScheduler scheduler)  {
        setResult((TResult) state);
        cont.apply(state, IContinuation.END_CONTINUATION, scheduler);
    }
}
