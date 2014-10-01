package net.goldolphin.cate.partitioned;

import net.goldolphin.cate.IStore;

import java.util.HashMap;
import java.util.Map;

/**
 * A partitioned data store, which can be used with a {@link PartitionedScheduler} to avoid locks on data read/write.
 * @param <K> key type.
 * @param <V> value type.
 * @author goldolphin
 *         2014-09-30 19:39
 */
public class PartitionedStore<K, V> implements IStore<K, V> {
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

    @Override
    public V get(K key) {
        return maps[partitioner.partition(key, maps.length)].get(key);
    }

    @Override
    public void put(K key, V value) {
        maps[partitioner.partition(key, maps.length)].put(key, value);
    }

    @Override
    public V remove(K key) {
        return maps[partitioner.partition(key, maps.length)].remove(key);
    }

    @Override
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
