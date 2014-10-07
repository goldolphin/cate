package net.goldolphin.cate;

/**
 * Abstract base class that simplify usage of tasks.
 * @param <TResult> result type.
 * @author goldolphin
 *         2014-09-06 15:12
 */
public abstract class Task<TResult> implements ITask<TResult> {
    /**
     * Execute the task with <tt>null</tt> as init state.
     * @param scheduler
     */
    public void execute(IScheduler scheduler) {
        execute(null, scheduler);
    }

    /**
     * Execute the task with specified init state in specified scheduler.
     * @param state
     * @param scheduler
     */
    public void execute(Object state, IScheduler scheduler) {
        scheduler.schedule(state, buildContinuation(IContinuation.END_CONTINUATION));
    }

    /**
     * After this task completes, continue to execute the specified task.
     * @param task
     * @param <SResult>
     * @return
     */
    public <SResult> Task<SResult> continueWith(ITask<SResult> task) {
        return new SeqTask<TResult, SResult>(this, task);
    }

    /**
     * Flatten the result of the task.
     * @param <NResult>
     * @return
     */
    public <NResult> Task<NResult> flatten() {
        return new FlattenTask<NResult, ITask<NResult>>((ITask<ITask<NResult>>) this);
    }

    /**
     * After this task completes, flatten the result and continue to execute the specified task.
     * @param task
     * @param <SResult>
     * @return
     */
    public <SResult> Task<SResult> flattenAndContinueWith(ITask<SResult> task) {
        return this.flatten().continueWith(task);
    }

    /**
     * After this task completes, continue to execute the specified action.
     * @param action
     * @return
     */
    public Task<Unit> continueWith(Action1<TResult> action) {
        return continueWith(create(action));
    }

    /**
     * After this task completes, flatten the result recursively and continue to execute the specified action.
     * @param action
     * @param <T>
     * @return
     */
    public <T> Task<Unit> flattenAndContinueWith(Action1<T> action) {
        return flattenAndContinueWith(create(action));
    }

    /**
     * After this task completes, continue to execute the specified function.
     * @param func
     * @param <SResult>
     * @return
     */
    public <SResult> Task<SResult> continueWith(Func1<TResult, SResult> func) {
        return continueWith(create(func));
    }

    /**
     * After this task completes, flatten the result and continue to execute the specified function.
     * @param func
     * @param <T>
     * @param <SResult>
     * @return
     */
    public <T, SResult> Task<SResult> flattenAndContinueWith(Func1<T, SResult> func) {
        return flattenAndContinueWith(create(func));
    }

    /**
     * After this task completes, continue to execute the specified action.
     * {@link Context#resume} must be invoked to resume the control flow.
     * @param action
     * @param <SResult>
     * @return
     */
    public <SResult> Task<SResult> continueWith(ContextAction<TResult, SResult> action) {
        return continueWith(create(action));
    }

    /**
     * After this task completes, flatten the result and continue to execute the specified function.
     * {@link Context#resume} must be invoked to resume the control flow.
     * @param action
     * @param <SResult>
     * @return
     */
    public <SResult> Task<SResult> flattenAndContinueWith(ContextAction<TResult, SResult> action) {
        return flattenAndContinueWith(create(action));
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
        return new TaskWithInitState<T, TResult>(this, initState);
    }

    /**
     * Wrap this task with specified init state.
     * @param initState
     * @param <T>
     * @return
     */
    public <T> Task<TResult> withInitState(T ... initState) {
        return new TaskWithInitState<T[], TResult>(this, initState);
    }

    /**
     * Create a new task, which will execute this task in specified scheduler.
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
     * @param <T>
     * @param <TResult>
     * @return
     */
    public static <T, TResult> Task<TResult> create(Func1<T, TResult> func) {
        return new Func1Task<T, TResult>(func);
    }

    /**
     * Create a task from a action.
     * @param action
     * @return
     */
    public static Task<Unit> create(Action0 action) {
        return new Action0Task(action);
    }

    /**
     * Create a task from a action.
     * @param action
     * @param <T>
     * @return
     */
    public static <T> Task<Unit> create(Action1<T> action) {
        return new Action1Task<T>(action);
    }

    /**
     * Create a task from an {@link ContextAction}.
     * {@link Context#resume} must be invoked to resume the control flow.
     * @param action
     * @param <TResult>
     * @return
     */
    public static <T, TResult> Task<TResult> create(ContextAction<T, TResult> action) {
        return new ContextTask<T, TResult>(action);
    }

    /**
     * Create a task which will complete depending on results of specified tasks.
     * @param tasks
     * @return
     */
    public static <TResult> CollectTask<TResult> when(ContextAction<Object, TResult> action, ITask<?>... tasks) {
        return new ContextCollectTask<TResult>(action, tasks);
    }

    /**
     * Create a task which will complete when all specified tasks complete.<br />
     * Beware, {@link Context} of this task contains mutable status.
     * @param tasks
     * @return
     */
    public static WhenAllTask whenAll(ITask<?>... tasks) {
        return new WhenAllTask(tasks);
    }

    /**
     * Create a task which will complete when any specified task complete.
     * Beware, {@link Context} of this task contains mutable status.
     * @param tasks
     * @return
     */
    public static WhenAnyTask whenAny(ITask<?>... tasks) {
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
