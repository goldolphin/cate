package net.goldolphin.cate;

/**
 * A wrapper task which will execute the wrapped task in specified scheduler.
 * @author goldolphin
 *         2014-09-26 21:48
 */
public class TaskWithScheduler<TInput, TResult> extends Task<TInput, TResult> {
    private final ITask<TInput, TResult> task;
    private final IScheduler scheduler;

    public TaskWithScheduler(ITask<TInput, TResult> task, IScheduler scheduler) {
        this.task = task;
        this.scheduler = scheduler;
    }

    @Override
    public IContinuation buildContinuation(IContinuation cont) {
        return new Continuation(cont);
    }

    private static void apply(IContinuation cont, Object state, Environment environment, IScheduler scheduler, IScheduler newScheduler) {
        if (scheduler == newScheduler) {
            cont.apply(state, environment, scheduler);
        } else {
            newScheduler.schedule(cont, state, environment);
        }
    }

    public class Continuation implements IContinuation {
        private final IContinuation next;

        public Continuation(final IContinuation cont) {
            this.next = task.buildContinuation(new IContinuation() {
                @Override
                public void apply(Object state, Environment environment, IScheduler scheduler) {
                    TaskWithScheduler.apply(cont, state, environment.getParent(), scheduler, (IScheduler) environment.getValue());
                }
            });
        }

        @Override
        public void apply(Object state, Environment environment, IScheduler scheduler) {
            TaskWithScheduler.apply(next, state, environment.extend(scheduler), scheduler, TaskWithScheduler.this.scheduler);
        }
    }
}
