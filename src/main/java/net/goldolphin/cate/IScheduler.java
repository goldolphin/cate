package net.goldolphin.cate;

/**
 * A scheduler.
 * @author goldolphin
 *         2014-09-05 22:46
 */
public interface IScheduler {
    /**
     * Schedule and apply a continuation with specified input state.
     * @param cont the continuation to be applied.
     * @param state input state.
     * @param environment the environment to be passed to the continuation.
     */
    public void schedule(IContinuation cont, Object state, Environment environment);
}
