package net.goldolphin.cate.partitioned;

import net.goldolphin.cate.IContinuation;
import net.goldolphin.cate.IScheduler;
import net.goldolphin.cate.Task;

/**
 * A scheduler pool which schedule tasks using a partitioning algorithm.
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
     * Execute a task.
     * @param key
     * @param task
     */
    public void execute(K key, Task<?> task) {
        IScheduler scheduler = schedulers[partitioner.partition(key, schedulers.length)];
        task.execute(scheduler);
    }

    /**
     * Execute a task with init state.
     * @param key
     * @param task
     * @param state
     */
    public void execute(K key, Task<?> task, Object state) {
        IScheduler scheduler = schedulers[partitioner.partition(key, schedulers.length)];
        task.execute(state, scheduler);
    }

    /**
     * Execute a task with init state & continuation.
     * @param key
     * @param task
     * @param state
     */
    public void execute(K key, Task<?> task, Object state, IContinuation cont) {
        IScheduler scheduler = schedulers[partitioner.partition(key, schedulers.length)];
        task.execute(state, cont, scheduler);
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
