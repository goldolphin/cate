package net.goldolphin.cate;

/**
 * An abstract task which will complete depending on results of specified tasks.
 * @author goldolphin
 *         2014-09-13 15:43
 */
public abstract class CollectTask<TResult> extends Task<TResult> {
    private final ITask<?>[] tasks;

    public CollectTask(ITask<?>[] tasks) {
        this.tasks = tasks;
    }

    /**
     * Returns tasks to wait.
     *
     * @return
     */
    public ITask<?>[] getTasks() {
        return tasks;
    }

    @Override
    public IContinuation buildContinuation(IContinuation cont) {
        IContinuation[] conts = new IContinuation[tasks.length];
        IContinuation collectorCont = buildCollectorContinuation(cont);
        for (int i = 0; i < tasks.length; i ++) {
            conts[i] = tasks[i].buildContinuation(collectorCont);
        }
        return new DispatcherContinuation(conts);
    }

    /**
     * Builds a collector continuation.
     *
     * @return
     */
    protected abstract IContinuation buildCollectorContinuation(IContinuation cont);

    public static class DispatcherContinuation implements IContinuation {
        protected final IContinuation[] next;

        public DispatcherContinuation(IContinuation[] next) {
            this.next = next;
        }

        @Override
        public void apply(Object state, IScheduler scheduler) {
            for (IContinuation cont: next) {
                cont.apply(state, scheduler);
            }
        }
    }
}
