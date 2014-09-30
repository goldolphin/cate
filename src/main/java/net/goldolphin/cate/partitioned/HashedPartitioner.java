package net.goldolphin.cate.partitioned;

/**
 * Partitioner based on {@link Object#hashCode()}
 * @author goldolphin
 *         2014-09-30 21:00
 */
public class HashedPartitioner implements IPartitioner {
    @Override
    public int partition(Object key, int num) {
        return (key.hashCode() & Integer.MAX_VALUE) % num;
    }
}
