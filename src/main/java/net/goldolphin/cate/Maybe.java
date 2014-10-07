package net.goldolphin.cate;

/**
 * A container, which may or may not contain a valid value.
 * @author goldolphin
 *         2014-10-07 01:12
 */
public class Maybe<T> {
    private static final Maybe<?> NOTHING = new Maybe<Object>(null);

    private final T value;

    private Maybe(T value) {
        this.value = value;
    }

    /**
     * Returns whether this instance represents nothing.
     * @return
     */
    public boolean isNothing() {
        return this == NOTHING;
    }

    /**
     * Returns the contained value.
     * @return
     */
    public T get() {
        return value;
    }

    /**
     * Returns an instance representing nothing.
     * @param <T>
     * @return
     */
    public static <T> Maybe<T> nothing() {
        return (Maybe<T>) NOTHING;
    }

    /**
     * Returns an instance containing the specified value.
     * @param value
     * @param <T>
     * @return
     */
    public static <T> Maybe<T> just(T value) {
        return new Maybe<T>(value);
    }

    @Override
    public String toString() {
        return isNothing() ? "Maybe{Nothing}" :
                "Maybe{" +
                "Just " + value +
                '}';
    }
}
