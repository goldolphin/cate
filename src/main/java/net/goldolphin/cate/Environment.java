package net.goldolphin.cate;

/**
 * An environment is a set of runtime data, which can be changed/read in {@link IContinuation#apply}.
 * @author goldolphin
 *         2014-11-15 23:41
 */
public class Environment {
    private static final Environment EMPTY = new Environment(null, null);

    private final Object value;
    private final Environment parent;

    private Environment(Object value, Environment parent) {
        this.value = value;
        this.parent = parent;
    }

    public Object getValue() {
        return value;
    }

    public Environment getParent() {
        return parent;
    }

    public Environment extend(Object value) {
        return new Environment(value, this);
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

    public static Environment empty() {
        return EMPTY;
    }
}
