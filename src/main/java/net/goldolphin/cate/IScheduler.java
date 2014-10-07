package net.goldolphin.cate;

/**
 * A scheduler.
 * @author goldolphin
 *         2014-09-05 22:46
 */
public interface IScheduler {
    /**
     * Schedule and apply a continuation with specified input state.
     * @param state
     * @param cont
     */
    public void schedule(Object state, IContinuation cont);
}
