package net.goldolphin.cate.partitioned;

import net.goldolphin.cate.IScheduler;

/**
 * A scheduler pool which is used to schedule tasks using a partitioning algorithm.
 * @param <K> the key type used to select scheduler.
 * @author goldolphin
 *         2014-09-30 19:26
 */
public class PartitionedSchedulerPool<K> {
    private final IScheduler[] schedulers;
    private final IPartitioner<K> partitioner;

    /**
     * Constructor.
     * @param schedulers
     * @param partitioner
     */
    public PartitionedSchedulerPool(IScheduler[] schedulers, IPartitioner<K> partitioner) {
        this.schedulers = schedulers;
        this.partitioner = partitioner;
    }

    /**
     * Returns a scheduler for a specified key.
     * @param key
     * @return
     */
    public IScheduler getScheduler(K key) {
        return schedulers[partitioner.partition(key, schedulers.length)];
    }

    /**
     * Get partition number.
     * @return
     */
    public int getPartitionNum() {
        return schedulers.length;
    }

    /**
     * Get the partitioner
     * @return
     */
    public IPartitioner getPartitioner() {
        return partitioner;
    }
}
