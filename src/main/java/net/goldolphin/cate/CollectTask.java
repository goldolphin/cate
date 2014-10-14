package net.goldolphin.cate;

/**
 * An abstract task which will complete depending on results of specified tasks.
 * @author goldolphin
 *         2014-09-13 15:43
 */
public abstract class CollectTask<TResult> extends Task<TResult> {
    protected final ITask<?>[] tasks;

    public CollectTask(ITask<?>[] tasks) {
        this.tasks = tasks;
    }

    /**
     * Returns tasks to wait.
     *
     * @return
     */
    public ITask<?>[] getTasks() {
        return tasks;
    }
}
