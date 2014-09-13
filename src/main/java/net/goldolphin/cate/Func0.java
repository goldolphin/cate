package net.goldolphin.cate;

/**
 * Function with no parameters.
 * @param <TResult> result type.
 * @author goldolphin
 *         2014-09-06 16:59
 */
public interface Func0<TResult> {
    /**
     * Apply the function.
     * @return
     */
    public TResult apply();
}
