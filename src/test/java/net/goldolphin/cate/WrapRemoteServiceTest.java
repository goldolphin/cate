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
        final AsyncClient<String, Integer, Boolean> client = new AsyncClient<String, Integer, Boolean>(store, evenChecker);

        // Run
        try {
            evenChecker.start(new Service.Handler<String, Boolean>() {
                @Override
                public void onReceive(String key, Boolean result) {
                    schedulerPool.execute(key, client.onReceive(key, result));
                }
            });

            Waiter<Boolean>[] waiters = (Waiter<Boolean>[]) new Waiter<?>[10];
            Random random = new Random();
            for (int i = 0; i < waiters.length; i ++) {
                String key = Long.toHexString(random.nextLong());
                Waiter<Boolean> waiter = client.call(key, i).continueWithWaiter();
                schedulerPool.execute(key, waiter);
                waiters[i] = waiter;
            }
            for (int i = 0; i < waiters.length; i ++) {
                Assert.assertEquals(i % 2 == 0, waiters[i].getResult());
            }
        } finally {
            evenChecker.shutdown();
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
        private final IStore<K, Context<Unit, V2>> store;
        private final Service<K, V1, V2> service;
        private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        public AsyncClient(IStore<K, Context<Unit, V2>> store, Service<K, V1, V2> service) {
            this.store = store;
            this.service = service;
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

        public Task<Unit> onReceive(final K key, final V2 result) {
            return Task.create(new Action0() {
                @Override
                public void apply() {
                    Context<Unit, V2> context = store.remove(key);
                    if (context != null) {
                        context.resume(result);
                    }
                }
            });
        }
    }
}
