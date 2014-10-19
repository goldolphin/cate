package net.goldolphin.cate;

/**
 * An abstract task which will complete depending on results of specified tasks.
 * @author goldolphin
 *         2014-09-13 15:43
 */
public abstract class CollectTask<TInput, TResult> extends Task<TInput, TResult> {
    protected final ITask<TInput, ?>[] tasks;

    public CollectTask(ITask<TInput, ?>[] tasks) {
        this.tasks = tasks;
    }

    /**
     * Returns tasks to wait.
     *
     * @return
     */
    public ITask<TInput, ?>[] getTasks() {
        return tasks;
    }
}
