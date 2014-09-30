package net.goldolphin.cate.partitioned;

import java.util.HashMap;
import java.util.Map;

/**
 * A partitioned data store, which can be used with a {@link PartitionedScheduler} to avoid locks on data read/write.
 * @author goldolphin
 *         2014-09-30 19:39
 */
public class PartitionedStore<K, V> {
    private final Map<K, V>[] maps;
    private final IPartitioner partitioner;

    /**
     * Construct a store using the same partitioning configuration as the specified {@link PartitionedScheduler}
     * @param scheduler
     */
    public PartitionedStore(PartitionedScheduler scheduler) {
        this(scheduler.getPartitionNum(), scheduler.getPartitioner());
    }

    /**
     * Construct a store.
     * @param partitionNum
     * @param partitioner
     */
    public PartitionedStore(int partitionNum, IPartitioner partitioner) {
        maps = (HashMap<K, V>[]) new HashMap<?, ?>[partitionNum];
        for (int i = 0; i < maps.length; i ++) {
            maps[i] = new HashMap<K, V>();
        }
        this.partitioner = partitioner;
    }

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this store contains no mapping for the key.
     * @param key
     * @return
     */
    public V get(K key) {
        return maps[partitioner.partition(key, maps.length)].get(key);
    }

    /**
     * Associates the specified value with the specified key in this store
     * (optional operation).  If the map previously contained a mapping for
     * the key, the old value is replaced by the specified value.
     * @param key
     * @param value
     */
    public void put(K key, V value) {
        maps[partitioner.partition(key, maps.length)].put(key, value);
    }

    /**
     * Removes the mapping for a key from this map if it is present
     * (optional operation).
     * <p>Returns the value to which this map previously associated the key,
     * or <tt>null</tt> if the map contained no mapping for the key.
     * @param key
     * @return
     */
    public V remove(K key) {
        return maps[partitioner.partition(key, maps.length)].remove(key);
    }

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the specified key.
     * @param key
     * @return
     */
    public boolean contains(K key) {
        return maps[partitioner.partition(key, maps.length)].containsKey(key);
    }

    /**
     * Returns the internal data, for test only.
     * @return
     */
    Map<K, V>[] getMaps() {
        return maps;
    }
}
