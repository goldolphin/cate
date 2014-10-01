package net.goldolphin.cate.partitioned;

import net.goldolphin.cate.Action1;
import net.goldolphin.cate.Context;
import net.goldolphin.cate.ExecutorScheduler;
import net.goldolphin.cate.Func1;
import net.goldolphin.cate.IScheduler;
import net.goldolphin.cate.IStore;
import net.goldolphin.cate.SynchronizedScheduler;
import net.goldolphin.cate.Task;
import net.goldolphin.cate.Waiter;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PartitionedSchedulerTest {
    @Test
    public void testScheduler() throws Exception {
        int partitionNum = 10;
        IScheduler[] schedulers = new IScheduler[partitionNum];
        for (int i = 0; i < partitionNum; i ++) {
            schedulers[i] = new SynchronizedScheduler();
        }
        PartitionedScheduler scheduler = new PartitionedScheduler(schedulers, new IdentityKeyExtractor(), new HashedPartitioner());
        final PartitionedStore<Integer, IScheduler> store = new PartitionedStore<Integer, IScheduler>(scheduler);
        Task<Boolean> task = Task.create(new Action1<Context<Integer, Boolean>>() {
            @Override
            public void apply(Context<Integer, Boolean> context) {
                Integer key = context.getState();
                IScheduler scheduler = store.get(key);
                if (scheduler == null) {
                    scheduler = context.getScheduler();
                    store.put(key, scheduler);
                } else {
                    System.out.format("%d. stored scheduler: %s, current scheduler: %s\n", key, scheduler, context.getScheduler());
                }
                context.resume(scheduler == context.getScheduler());
            }
        });

        for (int i = 0; i < 100; i ++) {
            Waiter<Boolean> waiter = task.continueWithWaiter();
            waiter.execute(i, scheduler);
            Assert.assertTrue(waiter.getResult());
        }

        for (int i = 0; i < 10; i ++) {
            Waiter<Boolean> waiter = task.continueWithWaiter();
            waiter.execute(i, scheduler);
            Assert.assertTrue(waiter.getResult());
        }

        for (Map<Integer, IScheduler> map: store.getMaps()) {
            System.out.println(map);
            IScheduler s0 = null;
            for (IScheduler s: map.values()) {
                if (s0 == null) {
                    s0 = s;
                } else {
                    Assert.assertEquals(s0, s);
                }
            }
        }
    }

    @Test
    public void testWrapAsync() throws Exception {
        // Initialize the scheduler & the store.
        int partitionNum = 10;
        IScheduler[] schedulers = new IScheduler[partitionNum];
        for (int i = 0; i < partitionNum; i ++) {
            // We use multiple threads here.
            schedulers[i] = new ExecutorScheduler(Executors.newSingleThreadExecutor());
        }
        PartitionedScheduler scheduler = new PartitionedScheduler(schedulers, new IdentityKeyExtractor(), new HashedPartitioner());
        PartitionedStore<String, Context<Pair<String, Integer>, Boolean>> store
                = new PartitionedStore<String, Context<Pair<String, Integer>, Boolean>>(scheduler);

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
                    client.onReceive(key, result);
                }
            });

            Waiter<Boolean>[] waiters = (Waiter<Boolean>[]) new Waiter<?>[10];
            Random random = new Random();
            for (int i = 0; i < waiters.length; i ++) {
                String key = Long.toHexString(random.nextLong());
                Waiter<Boolean> waiter = client.call(key, i).continueWithWaiter();
                waiter.execute(scheduler);
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

        public void send(Pair<K, V1> request) {
            System.out.format("Send request: %s -> %s.\n", request.key, request.value);
            try {
                queue.put(request);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public static interface Handler<K, V> {
            public void onReceive(K key, V result);
        }
    }

    public static class AsyncClient<K, V1, V2> {
        private final IStore<K, Context<Pair<K, V1>, V2>> store;
        private final Service<K, V1, V2> service;

        private final Task<V2> task = Task.create(new Action1<Context<Pair<K, V1>, V2>>() {
            @Override
            public void apply(Context<Pair<K, V1>, V2> context) {
                Pair<K, V1> request = context.getState();
                store.put(request.key, context);
                service.send(request);
            }
        });

        public AsyncClient(IStore<K, Context<Pair<K, V1>, V2>> store, Service<K, V1, V2> service) {
            this.store = store;
            this.service = service;
        }

        public Task<V2> call(K key, V1 input) {
            return task.withInitState(new Pair<K, V1>(key, input));
        }

        public void onReceive(K key, V2 result) {
            store.get(key).resume(result);
        }
    }
}