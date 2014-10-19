package net.goldolphin.cate;

/**
 * A wrapper task which will execute the wrapped task in specified scheduler.
 * @author goldolphin
 *         2014-09-26 21:48
 */
public class TaskWithScheduler<TInput, TResult> extends Task<TInput, TResult> {
    private final ITask<TInput, TResult> task;
    private final IContinuation cont;
    private final IScheduler scheduler;

    public TaskWithScheduler(ITask<TInput, TResult> task, IScheduler scheduler) {
        this.task = task;
        cont = task.buildContinuation(IContinuation.END_CONTINUATION);
        this.scheduler = scheduler;
    }

    @Override
    public IContinuation buildContinuation(IContinuation cont) {
        return new TaskContinuation<TInput>(cont, this);
    }

    @Override
    public void onExecute(Object state, IContinuation cont, IScheduler scheduler) {
        if (scheduler == this.scheduler) {
            this.cont.apply(state, cont, scheduler);
        } else {
            this.scheduler.schedule(state, this.cont, new Continuation(cont, scheduler));
        }
    }

    public static class Continuation implements IContinuation {
        private final IContinuation next;
        private final IScheduler scheduler;

        public Continuation(IContinuation next, IScheduler scheduler) {
            this.next = next;
            this.scheduler = scheduler;
        }

        @Override
        public void apply(Object state, IContinuation subCont, IScheduler scheduler) {
            this.scheduler.schedule(state, next, subCont);
        }
    }
}
