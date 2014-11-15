package net.goldolphin.cate;

/**
 * A task which will complete when all specified tasks complete.
 * @author goldolphin
 *         2014-09-06 18:27
 */
public class WhenAllTask<TInput> extends CollectTask<TInput, Object[]> {
    private final IContinuation[] conts;

    public WhenAllTask(ITask<TInput, ?> ... tasks) {
        super(tasks);
        conts = new IContinuation[tasks.length];
        for (int i = 0; i < tasks.length; i ++) {
            conts[i] = tasks[i].buildContinuation(new IndexContinuation(i, IContinuation.END_CONTINUATION));
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
        private final Object[] results;
        private int complete = 0;

        public Continuation(IContinuation next) {
            this.next = next;
            results = new Object[getTasks().length];
        }

        @Override
        public void apply(Object state, IContinuation subCont, IScheduler scheduler) {
            complete += 1;
            int total = getTasks().length;
            if (complete > total) {
                throw new IllegalStateException("Invalid complete value: " + complete + " exceeds " + total);
            }
            Result result = (Result) state;
            results[result.index] = result.value;
            if (complete == total) {
                next.apply(results, subCont, scheduler);
            }
        }
    }
}
