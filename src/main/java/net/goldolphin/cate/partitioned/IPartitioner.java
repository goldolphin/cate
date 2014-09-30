package net.goldolphin.cate.partitioned;

/**
 * Interface to map a key to a partition.
 * @author goldolphin
 *         2014-09-30 19:31
 */
public interface IPartitioner {
    /**
     * Map a key to a partition.
     * @param key
     * @param num
     * @return
     */
    public int partition(Object key, int num);
}
