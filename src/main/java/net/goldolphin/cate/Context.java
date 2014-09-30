package net.goldolphin.cate;

/**
 * Context of current control flow.
 * @param <TInput>
 * @param <TResult>
 */
public class Context<TInput, TResult> {
    private final TInput state;
    private final IContinuation cont;
    private final ITask<?> previous;
    private final IScheduler scheduler;

    /**
     * Constructor.
     * @param state
     * @param cont
     * @param previous
     * @param scheduler
     */
    Context(TInput state, IContinuation cont, ITask<?> previous, IScheduler scheduler) {
        this.previous = previous;
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
     * Resume the control flow with specified new state.
     * @param newState
     */
    public void resume(TResult newState) {
        resume(newState, scheduler);
    };

    /**
     * Resume the control flow with specified new state and scheduler.
     * @param newState
     */
    public void resume(TResult newState, IScheduler scheduler) {
        cont.apply(newState, previous, scheduler);
    };
}
