package net.goldolphin.cate;

/**
 * @author goldolphin
 *         2014-11-21 00:27
 */
public class IdenticalTask<T> extends Task<T, T> {
    private static final IdenticalTask<?> INSTANCE = new IdenticalTask<Object>();

    public static <T> IdenticalTask<T> instance() {
        return (IdenticalTask<T>) INSTANCE;
    }

    private IdenticalTask() {
    }

    @Override
    public IContinuation buildContinuation(IContinuation cont) {
        return cont;
    }
}
