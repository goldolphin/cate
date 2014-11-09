package net.goldolphin.cate;

/**
 * A task which will complete depending on results of specified tasks.
 * @author goldolphin
 *         2014-09-13 15:43
 */
public class ContextCollectTask<TInput, TResult> extends CollectTask<TInput, TResult> {
    private final ContextAction<Object, TResult> action;

    public ContextCollectTask(ContextAction<Object, TResult> action, ITask<TInput, ?> ... tasks) {
        super(tasks);
        this.action = action;
    }

    @Override
    public IContinuation buildContinuation(final IContinuation cont) {
        IContinuation[] conts = new IContinuation[tasks.length];
        IContinuation collectorCont = new IContinuation() {
            @Override
            public void apply(Object state, IContinuation subCont, IScheduler scheduler) {
                Context<Object, TResult> context = new Context<Object, TResult>(state, cont, subCont, scheduler);
                action.apply(context);
            }
        };
        for (int i = 0; i < tasks.length; i ++) {
            conts[i] = tasks[i].buildContinuation(collectorCont);
        }
        return new DispatcherContinuation(conts);
    }

    public static class DispatcherContinuation implements IContinuation {
        protected final IContinuation[] next;

        public DispatcherContinuation(IContinuation[] next) {
            this.next = next;
        }

        @Override
        public void apply(Object state, IContinuation subCont, IScheduler scheduler) {
            for (IContinuation cont: next) {
                cont.apply(state, subCont, scheduler);
            }
        }
    }
}
