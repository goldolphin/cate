package net.goldolphin.cate;

/**
 * Action with 1 parameter of type {@link Context}
 * @param <TInput>
 * @param <TResult>
 * @author goldolphin
 *         2014-10-02 00:15
 */
public interface ContextAction<TInput, TResult> {
    /**
     * Apply the action.
     * @param context
     */
    public void apply(Context<TInput, TResult> context);
}
