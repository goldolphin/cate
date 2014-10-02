package net.goldolphin.cate.partitioned;

/**
 * Partitioner based on {@link Object#hashCode()}
 * @author goldolphin
 *         2014-09-30 21:00
 */
public class HashedPartitioner<K> implements IPartitioner<K> {
    private static final HashedPartitioner<?> INSTANCE = new HashedPartitioner<Object>();

    /**
     * Get the partitioner singleton.
     * @param <K>
     * @return
     */
    public static final <K> HashedPartitioner<K> instance() {
        return (HashedPartitioner<K>) INSTANCE;
    }

    private HashedPartitioner() {}

    @Override
    public int partition(K key, int num) {
        return (key.hashCode() & Integer.MAX_VALUE) % num;
    }
}
