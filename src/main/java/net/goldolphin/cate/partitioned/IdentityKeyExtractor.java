package net.goldolphin.cate.partitioned;

/**
 * KeyExtractor that regard an object itself as its key.
 * @author goldolphin
 *         2014-09-30 21:29
 */
public class IdentityKeyExtractor implements KeyExtractor {
    @Override
    public Object extractKey(Object state) {
        return state;
    }
}
