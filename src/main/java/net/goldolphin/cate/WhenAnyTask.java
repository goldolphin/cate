package net.goldolphin.cate;

/**
 * A task which will complete when any specified task complete.
 * @author goldolphin
 *         2014-09-06 21:27
 */
public class WhenAnyTask<TInput> extends CollectTask<TInput, Object> {
    private final IContinuation[] conts;

    public WhenAnyTask(ITask<TInput, ?> ... tasks) {
        super(tasks);
        conts = new IContinuation[tasks.length];
        for (int i = 0; i < tasks.length; i ++) {
            conts[i] = tasks[i].buildContinuation(IContinuation.END_CONTINUATION);
        }
    }

    @Override
    public IContinuation buildContinuation(final IContinuation cont) {
        return new IContinuation() {
            @Override
            public void apply(Object state, IContinuation subCont, IScheduler scheduler) {
                IContinuation collectCont = new Continuation(SeqContinuation.seq(cont, subCont));
                for (IContinuation c: conts) {
                    c.apply(state, collectCont, scheduler);
                }
            }
        };
    }

    public class Continuation implements IContinuation {
        private final IContinuation next;
        private int complete = 0;

        public Continuation(IContinuation next) {
            this.next = next;
        }

        @Override
        public void apply(Object state, IContinuation subCont, IScheduler scheduler) {
            complete += 1;
            int total = getTasks().length;
            if (complete > total) {
                throw new IllegalStateException("Invalid complete value: " + complete + " exceeds " + total);
            } else if (complete == 1) {
                next.apply(state, subCont, scheduler);
            }
        }
    }
}
