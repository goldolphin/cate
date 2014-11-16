package net.goldolphin.cate;

/**
 * A task which will complete when all specified tasks complete.
 * @author goldolphin
 *         2014-09-06 18:27
 */
public class WhenAllTask<TInput> extends CollectTask<TInput, Object[]> {

    public WhenAllTask(ITask<TInput, ?> ... tasks) {
        super(tasks);
    }

    @Override
    public IContinuation buildContinuation(IContinuation cont) {
        IContinuation collectorCont = new Continuation(cont);
        final IContinuation[] conts = new IContinuation[tasks.length];
        for (int i = 0; i < tasks.length; i ++) {
            conts[i] = tasks[i].buildContinuation(new IndexContinuation(i, collectorCont));
        }

        return new IContinuation() {
            @Override
            public void apply(Object state, Environment environment, IScheduler scheduler) {
                Environment newEnv = environment.extend(new Counter(tasks.length));
                for (IContinuation c: conts) {
                    c.apply(state, newEnv, scheduler);
                }
            }
        };
    }

    public class Continuation implements IContinuation {
        private final IContinuation next;

        public Continuation(IContinuation next) {
            this.next = next;
        }

        @Override
        public void apply(Object state, Environment environment, IScheduler scheduler) {
            Counter counter = (Counter) environment.getValue();
            counter.record((Result) state);
            if (counter.getComplete() == tasks.length) {
                next.apply(counter.getResults(), environment.getParent(), scheduler);
            }
        }
    }
}
