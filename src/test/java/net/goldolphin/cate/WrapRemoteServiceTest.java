package net.goldolphin.cate;

import net.goldolphin.cate.partitioned.HashedPartitioner;
import net.goldolphin.cate.partitioned.PartitionedSchedulerPool;
import net.goldolphin.cate.partitioned.PartitionedStore;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author goldolphin
 *         2014-10-01 13:54
 */
public class WrapRemoteServiceTest {

    @Test
    public void testWrap() throws Exception {
        // Initialize the scheduler & the store.
        int partitionNum = 10;
        IScheduler[] schedulers = new IScheduler[partitionNum];
        for (int i = 0; i < partitionNum; i ++) {
            // We use multiple threads here.
            schedulers[i] = new ExecutorScheduler(Executors.newSingleThreadExecutor());
        }
        final PartitionedSchedulerPool<String> schedulerPool
                = new PartitionedSchedulerPool<String>(schedulers, HashedPartitioner.<String>instance());
        PartitionedStore<String, Context<Unit, Boolean>> store
                = new PartitionedStore<String, Context<Unit, Boolean>>(schedulerPool);

        // Initialize the service.
        Service<String, Integer, Boolean> evenChecker = new Service<String, Integer, Boolean>(new Func1<Integer, Boolean>() {
            @Override
            public Boolean apply(Integer value) {
                return value % 2 == 0;
            }
        });

        // Initialize the async client.
        Timer timer = new Timer();
        final AsyncClient<String, Integer, Boolean> client = new AsyncClient<String, Integer, Boolean>(evenChecker, store, timer);

        // Run
        try {
            evenChecker.start(new Service.Handler<String, Boolean>() {
                @Override
                public void onReceive(String key, Boolean result) {
                    client.onReceive(key, result).execute(schedulerPool.getScheduler(key));
                }
            });

            Waiter<Maybe<Boolean>>[] waiters = (Waiter<Maybe<Boolean>>[]) new Waiter<?>[10];
            Random random = new Random();
            for (int i = 0; i < waiters.length; i ++) {
                String key = Long.toHexString(random.nextLong());
                Waiter<Maybe<Boolean>> waiter = client.call(key, i, 100, TimeUnit.MILLISECONDS).continueWithWaiter();
                waiter.execute(schedulerPool.getScheduler(key));
                waiters[i] = waiter;
            }
            for (int i = 0; i < waiters.length; i ++) {
                Assert.assertEquals(i % 2 == 0, waiters[i].getResult().get());
            }
        } finally {
            evenChecker.shutdown();
            timer.shutdown();
        }
    }

    public static class Pair<K, V> {
        public final K key;
        public final V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    /**
     * A Fake service.
     * @param <K>
     * @param <V1>
     * @param <V2>
     */
    public static class Service<K, V1, V2> {
        private final Func1<V1, V2> logic;
        private final ExecutorService executor;
        private final BlockingQueue<Pair<K, V1>> queue = new ArrayBlockingQueue<Pair<K, V1>>(100);

        public Service(Func1<V1, V2> logic) {
            this.logic = logic;
            executor = Executors.newSingleThreadExecutor();
        }

        public void start(final Handler<K, V2> handler) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Pair<K, V1> request = queue.take();
                            V2 result = logic.apply(request.value);
                            System.out.format("Receive response: %s -> %s.\n", request.key, result);
                            handler.onReceive(request.key, result);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        }

        public void shutdown() {
            executor.shutdown();
        }

        public void send(K key, V1 input) {
            System.out.format("Send request: %s -> %s.\n", key, input);
            try {
                queue.put(new Pair<K, V1>(key, input));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public static interface Handler<K, V> {
            public void onReceive(K key, V result);
        }
    }

    /**
     * A demo async client.
     * @param <K>
     * @param <V1>
     * @param <V2>
     */
    public static class AsyncClient<K, V1, V2> {
        private final Service<K, V1, V2> service;
        private final IStore<K, Context<Unit, V2>> store;
        private final Timer timer;

        public AsyncClient(Service<K, V1, V2> service, IStore<K, Context<Unit, V2>> store, Timer timer) {
            this.service = service;
            this.store = store;
            this.timer = timer;
        }

        public Task<V2> call(final K key, final V1 input) {
            return Task.create(new ContextAction<Unit, V2>() {
                @Override
                public void apply(Context<Unit, V2> context) {
                    store.put(key, context);
                    service.send(key, input);
                }
            });
        }

        public Task<Maybe<V2>> call(K key, V1 input, long timeout, TimeUnit unit) {
            return timer.withTimeout(call(key, input), timeout, unit);
        }

        public Task<Unit> onReceive(final K key, final V2 result) {
            return Task.create(new Action0() {
                @Override
                public void apply() {
                    Context<Unit, V2> context = store.remove(key);
                    if (context != null) {
                        context.resumeSynchronously(result);
                    }
                }
            });
        }
    }

    public static class Timer {
        private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        public void shutdown() {
            executor.shutdown();
        }

        public <T> Task<T> delay(final T result, final long delay, final TimeUnit unit) {
            return Task.create(new ContextAction<Object, T>() {
                @Override
                public void apply(final Context<Object, T> context) {
                    executor.schedule(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("Timer triggered.");
                            context.resume(result);
                        }
                    }, delay, unit);
                }
            });
        }

        public <T> Task<Maybe<T>> withTimeout(ITask<T> task, long timeout, TimeUnit unit) {
            final Object timeoutFlag = new Object();
            return Task.whenAny(task, delay(timeoutFlag, timeout, unit)).continueWith(new Func1<WhenAnyTask.Result, Maybe<T>>() {
                @Override
                public Maybe<T> apply(WhenAnyTask.Result value) {
                    return value.result == timeoutFlag ? Maybe.<T>nothing() : Maybe.just((T)value.result);
                }
            });
        }
    }
}
