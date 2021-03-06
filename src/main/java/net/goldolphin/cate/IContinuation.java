package net.goldolphin.cate;

/**
 * A Continuation.
 * @author goldolphin
 *         2014-09-06 14:22
 */
public interface IContinuation {
    /**
     * Continuation representing that nothing need to do.
     */
    public static IContinuation END_CONTINUATION = new IContinuation() {
        @Override
        public void apply(Object state, Environment environment, IScheduler scheduler) {
        }

        @Override
        public String toString() {
            return "END_CONTINUATION";
        }
    };

    /**
     * Apply the continuation.
     * @param state input state.
     * @param environment the environment, a set of runtime data.
     * @param scheduler current scheduler.
     */
    public void apply(Object state, Environment environment, IScheduler scheduler);
}
