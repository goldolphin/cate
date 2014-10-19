package net.goldolphin.cate;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.Executors;

public class TaskTest {
    @Test
    public void testTask() throws Exception {
        // Executed in current thread.
        // We use a Waiter to force main thread to wait the async task.
        // The Waiter, which can be considered as a traditional java future, is a friendly utility for testing.
        // Don't use it in a pure async program, for it may block the execution.
        Waiter<Unit, Integer> waiter1 = testAsync(1).continueWithWaiter();
        waiter1.execute(new DebugScheduler());
        System.out.println(waiter1.getResult());
        Assert.assertEquals(40, waiter1.getResult().intValue());

        // Executed in a thread pool.
        Waiter<Unit, Integer> waiter2 = testAsync(2).continueWithWaiter();
        waiter2.execute(new ExecutorScheduler(Executors.newSingleThreadExecutor()));
        System.out.println(waiter2.getResult());
        Assert.assertEquals(50, waiter2.getResult().intValue());
    }

    /**
     * Build an async method running on a thread pool.
     * @param a
     * @param b
     * @param func
     */
    public static void addCallback(final int a, final int b, final Action1<Integer> func) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                func.apply(a + b);
            }
        });
    }

    /**
     * Wrap a callback based async method.
     * @param a
     * @param b
     * @return
     */
    public static Task<Unit, Integer> addAsync(int a, int b) {
        return addAsyncTask.withInitState(new int[]{a, b});
    }

    /**
     * This task is stateless, so we can build the task once and always reuse it.
     * Most primitive tasks are stateless, but {@link net.goldolphin.cate.Waiter} is stateful.
     */
    private static final Task<int[], Integer> addAsyncTask = Task.create(new ContextAction<int[], Integer>() {
        @Override
        public void apply(final Context<int[], Integer> context) {
            int a = context.getState()[0];
            int b = context.getState()[1];
            addCallback(a, b, new Action1<Integer>() {
                @Override
                public void apply(Integer value) {
                    context.resume(value);
                }
            });
        }
    });

    /**
     * We combine several async actions to build a new one.
     * Don't Repeat Yourself when such action sequence must be reused.
     * @return
     */
    public Task<Unit, Integer> testAsync(int value) {
        return testAsyncTask.withInitState(value);
    }

    // This task is also stateless, so we can build the task once and always reuse it.
    private static final Task<Integer, Integer> testAsyncTask = Task.create(new Func1<Integer, Integer>() {
        // Create a task from a normal function.
        @Override
        public Integer apply(Integer value) {
            return value;
        }
    }).continueWith(new ContextAction<Integer, Integer>() {
        // Serialize tasks.
        @Override
        public void apply(final Context<Integer, Integer> context) {
            // Invoke callback based async function directly.
            addCallback(context.getState(), 1, new Action1<Integer>() {
                @Override
                public void apply(Integer value) {
                    context.resume(value);
                }
            });
        }
    }).continueWith(new Func1<Integer, ITask<Unit, Integer>>() {
        @Override
        public ITask<Unit, Integer> apply(Integer value) {
            // A multi-branch dispatch.
            if (value == 1) {
                // Run an async function to produce a nested task: ITask<ITask<Integer>>.
                return addAsync(value, 1);
            }
            return addAsync(value, 2);
        }
    }).<Integer>flatten() // We must flatten the nested task(schedule the nested task) before we use the Integer.
      .continueWith(
              Task.whenAll(
                      // Spawn 2 tasks.
                      Task.create(new Func1<Integer, Integer>() {
                          @Override
                          public Integer apply(Integer value) {
                              return value * 2;
                          }
                      }),
                      Task.create(new Func1<Integer, Integer>() {
                          @Override
                          public Integer apply(Integer value) {
                              return value * 3;
                          }
                      }))
      ).continueWith(new Func1<List<Object>, Integer>() {
                // Collect results of the 2 task2
                @Override
                public Integer apply(List<Object> value) {
                    return (Integer) value.get(0) + (Integer) value.get(1);
                }
            }).continueWith(new Func1<Integer, Integer>() {
                @Override
                public Integer apply(Integer value) {
                    return value * 2;
                }
            });

    public static class DebugScheduler extends SynchronizedScheduler {
        @Override
        public void schedule(Object state, IContinuation cont, IContinuation subCont) {
            System.out.format("Run: state=%s, cont=%s, subCont=%s, scheduler=%s\n",
                    state, cont, subCont, this);
            super.schedule(state, cont, subCont);
        }
    }
}
