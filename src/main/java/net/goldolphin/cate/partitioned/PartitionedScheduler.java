package net.goldolphin.cate.partitioned;

import net.goldolphin.cate.IContinuation;
import net.goldolphin.cate.IScheduler;
import net.goldolphin.cate.ITask;

/**
 * A combined scheduler which schedule tasks in an internal scheduler pool using a partitioning algorithm.
 * @author goldolphin
 *         2014-09-30 19:26
 */
public class PartitionedScheduler implements IScheduler {
    private final IScheduler[] schedulers;
    private final KeyExtractor keyExtractor;
    private final IPartitioner partitioner;

    /**
     * Constructor.
     * @param schedulers
     * @param keyExtractor
     * @param partitioner
     */
    public PartitionedScheduler(IScheduler[] schedulers, KeyExtractor keyExtractor, IPartitioner partitioner) {
        this.schedulers = schedulers;
        this.keyExtractor = keyExtractor;
        this.partitioner = partitioner;
    }

    @Override
    public void schedule(ITask<?> task, Object state, IContinuation cont, ITask<?> previous) {
        IScheduler scheduler = schedulers[partitioner.partition(keyExtractor.extractKey(state), schedulers.length)];
        scheduler.schedule(task, state, cont, previous);
    }

    /**
     * Get partition number.
     * @return
     */
    public int getPartitionNum() {
        return schedulers.length;
    }

    /**
     * Get the key extractor.
     * @return
     */
    public KeyExtractor getKeyExtractor() {
        return keyExtractor;
    }

    /**
     * Get the partitioner
     * @return
     */
    public IPartitioner getPartitioner() {
        return partitioner;
    }
}
