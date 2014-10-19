package net.goldolphin.cate;

/**
 * Context of current control flow.
 * @param <TInput>
 * @param <TResult>
 */
public class Context<TInput, TResult> {
    private final TInput state;
    private final IContinuation cont;
    private final IScheduler scheduler;

    /**
     * Constructor.
     * @param state
     * @param cont
     * @param scheduler
     */
    Context(TInput state, IContinuation cont, IScheduler scheduler) {
        this.state = state;
        this.cont = cont;
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
        scheduler.schedule(newState, cont, IContinuation.END_CONTINUATION);
    };

    /**
     * Resume the control flow with specified new state synchronously.
     * @param newState
     */
    public void resumeSynchronously(TResult newState) {
        cont.apply(newState, IContinuation.END_CONTINUATION, scheduler);
    }
}
