package net.goldolphin.cate;

/**
 * A scheduler that execute continuations synchronously.
 * @author goldolphin
 *         2014-09-06 15:10
 */
public class SynchronizedScheduler implements IScheduler {
    @Override
    public void schedule(IContinuation cont, Object state, Environment environment) {
        cont.apply(state, environment, this);
    }
}
