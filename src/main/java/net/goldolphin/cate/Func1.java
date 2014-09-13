package net.goldolphin.cate;

/**
 * Function with 1 parameter.
 * @param <T> parameter type.
 * @param <TResult> result type.
 * @author goldolphin
 *         2014-09-05 22:47
 */
public interface Func1<T, TResult> {
    /**
     * Apply the function.
     * @param value
     * @return
     */
    public TResult apply(T value);
}
