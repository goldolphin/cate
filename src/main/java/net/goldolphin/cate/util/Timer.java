package net.goldolphin.cate.util;

import net.goldolphin.cate.Context;
import net.goldolphin.cate.ContextAction;
import net.goldolphin.cate.Func1;
import net.goldolphin.cate.ITask;
import net.goldolphin.cate.Maybe;
import net.goldolphin.cate.Task;

import java.util.concurrent.TimeUnit;

/**
 * An abstract class for handling timeout.
 * @author goldolphin
 *         2014-10-07 11:29
 */
public abstract class Timer {
    /**
     * Resume a context after the given delay.
     * @param context
     * @param result
     * @param delay
     * @param unit
     * @param <T>
     */
    public abstract <T> void resumeAfter(Context<?, T> context, T result, long delay, TimeUnit unit);

    /**
     * Create a task, which will yield the specified result after the given delay.
     * @param result
     * @param delay
     * @param unit
     * @param <TResult> the result type.
     * @return
     */
    public <TResult> Task<?, TResult> delay(final TResult result, final long delay, final TimeUnit unit) {
        return Task.create(new ContextAction<Object, TResult>() {
            @Override
            public void apply(final Context<Object, TResult> context) {
                resumeAfter(context, result, delay, unit);
            }
        });
    }

    /**
     * Wrap the specified task with timeout mechanism.
     * @param task
     * @param timeout
     * @param unit
     * @param <TInput> the input type.
     * @param <TResult> the result type.
     * @return A task which will yield {@link Maybe#nothing()} when wrapped task is completed not in time, otherwise the result.
     */
    public <TInput, TResult> Task<TInput, Maybe<TResult>> withTimeout(ITask<TInput, TResult> task, long timeout, TimeUnit unit) {
        final Object timeoutFlag = new Object();
        return Task.whenAny(task, (Task<TInput, Object>)delay(timeoutFlag, timeout, unit)).continueWith(new Func1<Object, Maybe<TResult>>() {
            @Override
            public Maybe<TResult> apply(Object value) {
                return value == timeoutFlag ? Maybe.<TResult>nothing() : Maybe.just((TResult)value);
            }
        });
    }
}
