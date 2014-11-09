package net.goldolphin.cate;

/**
 * A wrapper task which will execute the wrapped task in specified scheduler.
 * @author goldolphin
 *         2014-09-26 21:48
 */
public class TaskWithScheduler<TInput, TResult> extends Task<TInput, TResult> {
    private final IContinuation cont;
    private final IScheduler scheduler;

    public TaskWithScheduler(ITask<TInput, TResult> task, IScheduler scheduler) {
        cont = task.buildContinuation(IContinuation.END_CONTINUATION);
        this.scheduler = scheduler;
    }

    @Override
    public IContinuation buildContinuation(final IContinuation cont) {
        return new IContinuation() {
            @Override
            public void apply(Object state, IContinuation subCont, IScheduler scheduler) {
                if (scheduler == TaskWithScheduler.this.scheduler) {
                    TaskWithScheduler.this.cont.apply(state, SeqContinuation.seq(cont, subCont), scheduler);
                } else {
                    TaskWithScheduler.this.scheduler.schedule(
                            state,
                            TaskWithScheduler.this.cont,
                            new Continuation(SeqContinuation.seq(cont, subCont), scheduler));
                }
            }
        };
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
