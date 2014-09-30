package net.goldolphin.cate.partitioned;

/**
 * Interface to extract a key from an object.
 * @author goldolphin
 *         2014-09-30 21:26
 */
public interface KeyExtractor {
    /**
     * Extract a key from an object.
     * @param obj
     * @return
     */
    public Object extractKey(Object obj);
}
