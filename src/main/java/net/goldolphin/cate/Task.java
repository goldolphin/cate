package net.goldolphin.cate;

/**
 * Abstract base class that simplify usage of tasks.<p />
 * For a general view of a task, please refer to {@link net.goldolphin.cate.ITask}
 *
 * @param <TInput> the input type.
 * @param <TResult> result type.
 * @author goldolphin
 *         2014-09-06 15:12
 */
public abstract class Task<TInput, TResult> implements ITask<TInput, TResult> {
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
        scheduler.schedule(buildContinuation(IContinuation.END_CONTINUATION), state, Environment.empty());
    }

    /**
     * After this task completes, continue to execute the specified task.
     * @param task
     * @param <SResult>
     * @return
     */
    public <SResult> Task<TInput, SResult> continueWith(ITask<TResult, SResult> task) {
        return new SeqTask<TInput, TResult, SResult>(this, task);
    }

    /**
     * Flatten the result of the task.
     * @param <NResult>
     * @return
     */
    public <NResult> Task<TInput, NResult> flatten() {
        return new FlattenTask<TInput, NResult, ITask<Unit, NResult>>((ITask<TInput, ITask<Unit, NResult>>) this);
    }

    /**
     * After this task completes, continue to execute the specified action.
     * @param action
     * @return
     */
    public Task<TInput, Unit> continueWith(Action1<TResult> action) {
        return continueWith(create(action));
    }

    /**
     * After this task completes, continue to execute the specified function.
     * @param func
     * @param <SResult>
     * @return
     */
    public <SResult> Task<TInput, SResult> continueWith(Func1<TResult, SResult> func) {
        return continueWith(create(func));
    }

    /**
     * After this task completes, continue to execute the specified action.
     * {@link Context#resume} must be invoked to resume the control flow.
     * @param action
     * @param <SResult>
     * @return
     */
    public <SResult> Task<TInput, SResult> continueWith(ContextAction<TResult, SResult> action) {
        return continueWith(create(action));
    }

    /**
     * After this task completes, continue to execute a waiter.<br />
     * Beware, a waiter contains mutable status.
     * @return
     */
    public Waiter<TInput, TResult> continueWithWaiter() {
        return new Waiter<TInput, TResult>(this);
    }

    /**
     * Wrap this task with specified init state.
     * @param initState
     * @return
     */
    public Task<Unit, TResult> withInitState(TInput initState) {
        return new TaskWithInitState<TInput, TResult>(this, initState);
    }

    /**
     * Create a new task, which will execute this task in specified scheduler.
     * @param scheduler
     * @return
     */
    public Task<TInput, TResult> withScheduler(IScheduler scheduler) {
        return new TaskWithScheduler<TInput, TResult>(this, scheduler);
    }

    /**
     * Create a task from a function.
     * @param func
     * @param <TResult>
     * @return
     */
    public static <TResult> Task<Unit, TResult> create(Func0<TResult> func) {
        return new Func0Task<TResult>(func);
    }

    /**
     * Create a task from a function.
     * @param func
     * @param <TInput>
     * @param <TResult>
     * @return
     */
    public static <TInput, TResult> Task<TInput, TResult> create(Func1<TInput, TResult> func) {
        return new Func1Task<TInput, TResult>(func);
    }

    /**
     * Create a task from a action.
     * @param action
     * @return
     */
    public static Task<Unit, Unit> create(Action0 action) {
        return new Action0Task(action);
    }

    /**
     * Create a task from a action.
     * @param action
     * @param <TInput>
     * @return
     */
    public static <TInput> Task<TInput, Unit> create(Action1<TInput> action) {
        return new Action1Task<TInput>(action);
    }

    /**
     * Create a task from an {@link ContextAction}.
     * {@link Context#resume} must be invoked to resume the control flow.
     * @param action
     * @param <TInput>
     * @param <TResult>
     * @return
     */
    public static <TInput, TResult> Task<TInput, TResult> create(ContextAction<TInput, TResult> action) {
        return new ContextTask<TInput, TResult>(action);
    }

    /**
     * Create a task which will complete depending on results of specified tasks.
     * @param action
     * @param tasks
     * @param <TInput>
     * @param <TResult>
     * @return
     */
    public static <TInput, TResult> CollectTask<TInput, TResult> when(ContextAction<CollectTask.Result, TResult> action, ITask<TInput, ?>... tasks) {
        return new ContextCollectTask<TInput, TResult>(action, tasks);
    }

    /**
     * Create a task which will complete when all specified tasks complete.<br />
     * @param tasks
     * @param <TInput>
     * @return
     */
    public static <TInput> WhenAllTask<TInput> whenAll(ITask<TInput, ?>... tasks) {
        return new WhenAllTask<TInput>(tasks);
    }

    /**
     * Create a task which will complete when any specified task complete.
     * @param tasks
     * @param <TInput>
     * @return
     */
    public static <TInput> WhenAnyTask<TInput> whenAny(ITask<TInput, ?>... tasks) {
        return new WhenAnyTask<TInput>(tasks);
    }
}
