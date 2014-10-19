package net.goldolphin.cate;

/**
 * A scheduler.
 * @author goldolphin
 *         2014-09-05 22:46
 */
public interface IScheduler {
    /**
     * Schedule and apply a continuation with specified input state.
     * @param state input state.
     * @param cont the continuation to be applied.
     * @param subCont subsequent continuation, which should be applied after current one is applied.
     */
    public void schedule(Object state, IContinuation cont, IContinuation subCont);
}
