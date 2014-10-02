package net.goldolphin.cate.partitioned;

/**
 * Interface to map a key to a partition.
 * @param <K> the key type used for partitioning.
 * @author goldolphin
 *         2014-09-30 19:31
 */
public interface IPartitioner<K> {
    /**
     * Map a key to a partition.
     * @param key
     * @param num
     * @return
     */
    public int partition(K key, int num);
}
