package net.goldolphin.cate;

/**
 * A task to wait the result of its antecedent.
 * @param <TResult> result type.
 * @author goldolphin
 *         2014-09-06 18:05
 */
public class Waiter<TResult> extends SeqTask<TResult, TResult> {
    private final Object lock = new Object();
    private volatile boolean isComplete = false;
    private volatile TResult result;

    public Waiter(ITask<TResult> antecedent) {
        super(antecedent, false);
    }

    /**
     * Whether the task is complete.
     * @return
     */
    public boolean isComplete() {
        return isComplete;
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
    protected TResult evaluate(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onExecute(Object state, IContinuation cont, ITask<?> previous, IScheduler scheduler)  {
        synchronized (lock) {
            result = (TResult) state;
            isComplete = true;
            lock.notifyAll();
        }
        cont.apply(state, this, scheduler);
    }
}
