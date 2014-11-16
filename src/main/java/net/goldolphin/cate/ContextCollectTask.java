package net.goldolphin.cate;

/**
 * A task which will complete depending on results of specified tasks.
 * @author goldolphin
 *         2014-09-13 15:43
 */
public class ContextCollectTask<TInput, TResult> extends CollectTask<TInput, TResult> {
    private final ContextAction<Result, TResult> action;

    public ContextCollectTask(ContextAction<Result, TResult> action, ITask<TInput, ?> ... tasks) {
        super(tasks);
        this.action = action;
    }

    @Override
    public IContinuation buildContinuation(final IContinuation cont) {
        IContinuation[] conts = new IContinuation[tasks.length];
        IContinuation collectorCont = new IContinuation() {
            @Override
            public void apply(Object state, Environment environment, IScheduler scheduler) {
                Context<Result, TResult> context = new Context<Result, TResult>((Result) state, cont, environment, scheduler);
                action.apply(context);
            }
        };
        for (int i = 0; i < tasks.length; i ++) {
            conts[i] = tasks[i].buildContinuation(new IndexContinuation(i, collectorCont));
        }
        return new DispatcherContinuation(conts);
    }

    public static class DispatcherContinuation implements IContinuation {
        protected final IContinuation[] next;

        public DispatcherContinuation(IContinuation[] next) {
            this.next = next;
        }

        @Override
        public void apply(Object state, Environment environment, IScheduler scheduler) {
            for (IContinuation cont: next) {
                cont.apply(state, environment, scheduler);
            }
        }
    }
}
