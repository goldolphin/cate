package net.goldolphin.cate;

/**
 * Action with 1 parameter.
 * @param <T> parameter type.
 * @author goldolphin
 *         2014-09-08 12:01
 */
public interface Action1<T> {
    /**
     * Apply the action.
     * @param value
     */
    public void apply(T value);
}
