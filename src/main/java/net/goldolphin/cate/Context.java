package net.goldolphin.cate;

/**
 * Context of current control flow.
 * @param <TInput>
 * @param <TResult>
 */
public class Context<TInput, TResult> {
    private final TInput state;
    private final IContinuation cont;
    private final IContinuation subCont;
    private final IScheduler scheduler;

    /**
     * Constructor.
     * @param state
     * @param cont
     * @param subCont
     * @param scheduler
     */
    Context(TInput state, IContinuation cont, IContinuation subCont, IScheduler scheduler) {
        this.state = state;
        this.cont = cont;
        this.subCont = subCont;
        this.scheduler = scheduler;
    }

    /**
     * Get current state.
     * @return
     */
    public TInput getState() {
        return state;
    }

    /**
     * Get current scheduler.
     * @return
     */
    public IScheduler getScheduler() {
        return scheduler;
    }

    /**
     * Resume the control flow with specified new state in former scheduler.
     * @param newState
     */
    public void resume(TResult newState) {
        scheduler.schedule(newState, cont, subCont);
    };
}
