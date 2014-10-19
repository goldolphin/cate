package net.goldolphin.cate;

/**
 * A type that allows only one value (and thus can hold no information). -- from Wikipedia
 * @author goldolphin
 *         2014-10-02 00:04
 */
public class Unit {
    /**
     * The unique value of type {@link Unit}
     */
    public static final Unit VALUE = new Unit();

    private Unit() {
    }
}
