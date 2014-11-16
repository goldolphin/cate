package net.goldolphin.cate;

/**
 * Context of current control flow.
 * @param <TInput>
 * @param <TResult>
 */
public class Context<TInput, TResult> {
    private final TInput state;
    private final IContinuation cont;
    private final Environment environment;
    private final IScheduler scheduler;

    /**
     * Constructor.
     * @param state
     * @param cont
     * @param environment
     * @param scheduler
     */
    Context(TInput state, IContinuation cont, Environment environment, IScheduler scheduler) {
        this.state = state;
        this.cont = cont;
        this.environment = environment;
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
        scheduler.schedule(cont, newState, environment);
    };
}
