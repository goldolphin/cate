package net.goldolphin.cate;

/**
 * Abstract base class that simplify usage of tasks.
 * @param <TResult> result type.
 * @author goldolphin
 *         2014-09-06 15:12
 */
public abstract class Task<TResult> implements ITask<TResult> {
    /**
     * Execute the task without any continuation.
     * @param scheduler
     */
    public void execute(IScheduler scheduler) {
        execute(null, scheduler);
    }

    /**
     * Execute the task with specified init state but no continuation.
     * @param state
     * @param scheduler
     */
    public void execute(Object state, IScheduler scheduler) {
        execute(state, IContinuation.END_CONTINUATION, scheduler);
    }

    /**
     * After this task completes, continue to execute the specified function.
     * @param func
     * @param <SResult>
     * @return
     */
    public <SResult> Task<SResult> continueWith(Func1<TResult, SResult> func) {
        return new Func1SeqTask<TResult, TResult, SResult>(this, func, false);
    }

    /**
     * After this task completes, flatten the result recursively and continue to execute the specified function.
     * @param func
     * @param <T>
     * @param <SResult>
     * @return
     */
    public <T, SResult> Task<SResult> flattenAndContinueWith(Func1<T, SResult> func) {
        return new Func1SeqTask<T, TResult, SResult>(this, func, true);
    }

    /**
     * After this task completes, continue to execute the specified action.
     * {@link Context#resume} must be invoked to resume the control flow.
     * @param action
     * @param <SResult>
     * @return
     */
    public <SResult> Task<SResult> continueWith(Action1<Context<TResult, SResult>> action) {
        return new ContextSeqTask<TResult, SResult>(this, action, false);
    }

    /**
     * After this task completes, flatten the result recursively and continue to execute the specified function.
     * {@link Context#resume} must be invoked to resume the control flow.
     * @param action
     * @param <SResult>
     * @return
     */
    public <SResult> Task<SResult> flattenAndContinueWith(Action1<Context<TResult, SResult>> action) {
        return new ContextSeqTask<TResult, SResult>(this, action, true);
    }

    /**
     * After this task completes, continue to execute a waiter.<br />
     * Beware, a waiter contains mutable status.
     * @return
     */
    public Waiter<TResult> continueWithWaiter() {
        return new Waiter<TResult>(this);
    }

    /**
     * Wrap this task with specified init state.
     * @param initState
     * @param <T>
     * @return
     */
    public <T> Task<TResult> withInitState(T initState) {
        return new TaskWithInitState<T, TResult>(initState, this);
    }

    /**
     * Wrap this task with specified init state.
     * @param initState
     * @param <T>
     * @return
     */
    public <T> Task<TResult> withInitState(T ... initState) {
        return new TaskWithInitState<T[], TResult>(initState, this);
    }

    /**
     * Wrap this task with specified scheduler.
     * @param scheduler
     * @return
     */
    public Task<TResult> withScheduler(IScheduler scheduler) {
        return new TaskWithScheduler<TResult>(this, scheduler);
    }

    /**
     * Create a task from a function.
     * @param func
     * @param <TResult>
     * @return
     */
    public static <TResult> Task<TResult> create(Func0<TResult> func) {
        return new Func0Task<TResult>(func);
    }

    /**
     * Create a task from a function.
     * @param func
     * @param <TResult>
     * @return
     */
    public static <T, TResult> Task<TResult> create(Func1<T, TResult> func) {
        return new Func1Task<T, TResult>(func);
    }

    /**
     * Create a task from an action.
     * {@link Context#resume} must be invoked to resume the control flow.
     * @param action
     * @param <TResult>
     * @return
     */
    public static <T, TResult> Task<TResult> create(Action1<Context<T, TResult>> action) {
        return new ContextTask<T, TResult>(action);
    }

    /**
     * Create a task which will complete depending on results of specified tasks.
     * @param tasks
     * @return
     */
    public static <TResult> CollectTask<TResult> continueWhen(Action1<Context<?, TResult>> action, ITask<?>... tasks) {
        return new ContextCollectTask<TResult>(action, tasks);
    }

    /**
     * Create a task which will complete when all specified tasks complete.<br />
     * Beware, {@link Context} of this task contains mutable status.
     * @param tasks
     * @return
     */
    public static WhenAllTask continueWhenAll(ITask<?>... tasks) {
        return new WhenAllTask(tasks);
    }

    /**
     * Create a task which will complete when any specified task complete.
     * Beware, {@link Context} of this task contains mutable status.
     * @param tasks
     * @return
     */
    public static WhenAnyTask continueWhenAny(ITask<?>... tasks) {
        return new WhenAnyTask(tasks);
    }

    /**
     * Flatten the result of the specified task non-recursively.
     * @param task
     * @param <TResult>
     * @param <TTask>
     * @return
     */
    public static <TResult, TTask extends ITask<TResult>> Task<TResult> flattenOnce(ITask<TTask> task) {
        return new FlattenTask<TResult, TTask>(task);
    }
}
