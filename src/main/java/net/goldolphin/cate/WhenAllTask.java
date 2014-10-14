package net.goldolphin.cate;

import java.util.ArrayList;
import java.util.List;

/**
 * A task which will complete when all specified tasks complete.
 * @author goldolphin
 *         2014-09-06 18:27
 */
public class WhenAllTask extends CollectTask<List<Object>> {

    public WhenAllTask(ITask<?> ... tasks) {
        super(tasks);
    }

    @Override
    public IContinuation buildContinuation(IContinuation cont) {
        return new net.goldolphin.cate.Continuation(cont, this);
    }

    @Override
    public void onExecute(Object state, IContinuation cont, IScheduler scheduler) {
        IContinuation collectorCont = new Continuation(cont, this);
        for (int i = 0; i < tasks.length; i ++) {
            tasks[i].buildContinuation(collectorCont).apply(state, scheduler);
        }
    }

    public static class Continuation implements IContinuation {
        private final IContinuation next;
        private final WhenAllTask task;
        private final List<Object> results;
        private int complete = 0;

        public Continuation(IContinuation next, WhenAllTask task) {
            this.next = next;
            this.task = task;
            results = new ArrayList<Object>(task.getTasks().length);
        }

        @Override
        public void apply(Object state, IScheduler scheduler) {
            complete += 1;
            int total = task.getTasks().length;
            if (complete > total) {
                throw new IllegalStateException("Invalid complete value: " + complete + " exceeds " + total);
            }
            results.add(state);
            if (complete == total) {
                next.apply(results, scheduler);
            }
        }
    }
}
