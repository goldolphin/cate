package net.goldolphin.cate;

/**
 * A task which will complete depending on results of specified tasks.
 * @author goldolphin
 *         2014-09-13 15:43
 */
public class ContextCollectTask<TResult> extends CollectTask<TResult> {
    private final ContextAction<Object, TResult> action;

    public ContextCollectTask(ContextAction<Object, TResult> action, ITask<?> ... tasks) {
        super(tasks);
        this.action = action;
    }

    @Override
    public IContinuation buildContinuation(IContinuation cont) {
        IContinuation[] conts = new IContinuation[tasks.length];
        IContinuation collectorCont = new TaskContinuation(cont, this);
        for (int i = 0; i < tasks.length; i ++) {
            conts[i] = tasks[i].buildContinuation(collectorCont);
        }
        return new DispatcherContinuation(conts);
    }

    @Override
    public void onExecute(Object state, IContinuation cont, IScheduler scheduler) {
        Context<Object, TResult> context = new Context<Object, TResult>(state, cont, scheduler);
        action.apply(context);
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
