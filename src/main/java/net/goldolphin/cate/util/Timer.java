package net.goldolphin.cate.util;

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
     * Create a task, which will yield the specified result in specified delay.
     * @param result
     * @param delay
     * @param unit
     * @param <T>
     * @return
     */
    public abstract <T> Task<T> delay(final T result, final long delay, final TimeUnit unit);

    /**
     * Wrap the specified task with timeout mechanism.
     * @param task
     * @param timeout
     * @param unit
     * @param <T>
     * @return A task which will yield {@link Maybe#nothing()} when wrapped task is completed not in time, otherwise the result.
     */
    public <T> Task<Maybe<T>> withTimeout(ITask<T> task, long timeout, TimeUnit unit) {
        final Object timeoutFlag = new Object();
        return Task.whenAny(task, delay(timeoutFlag, timeout, unit)).continueWith(new Func1<Object, Maybe<T>>() {
            @Override
            public Maybe<T> apply(Object value) {
                return value == timeoutFlag ? Maybe.<T>nothing() : Maybe.just((T)value);
            }
        });
    }
}
