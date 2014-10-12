package net.goldolphin.cate;

import net.goldolphin.cate.partitioned.HashedPartitioner;
import net.goldolphin.cate.partitioned.PartitionedSchedulerPool;
import net.goldolphin.cate.util.ExecutorTimer;
import net.goldolphin.cate.util.Timer;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A demo test, which demonstrate how to wrap a typical producer/consumer client into a Task-Style client. Timeout mechanism is added.
 * @author goldolphin
 *         2014-10-01 13:54
 */
public class WrapRemoteClientTest {

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

        // Initialize the service.
        Client<String, Integer, Boolean> evenChecker = new Client<String, Integer, Boolean>(new Func1<Integer, Boolean>() {
            @Override
            public Boolean apply(Integer value) {
                return value % 2 == 0;
            }
        });

        // Initialize the async client.
        final AsyncClient<String, Integer, Boolean> client = new AsyncClient<String, Integer, Boolean>(evenChecker);

        // Run
        try {
            Waiter<Maybe<Boolean>>[] waiters = (Waiter<Maybe<Boolean>>[]) new Waiter<?>[10];
            Random random = new Random();
            // Test when results come back in time.
            for (int i = 0; i < waiters.length; i++) {
                String key = Long.toHexString(random.nextLong());
                Waiter<Maybe<Boolean>> waiter = client.call(key, i, 100, TimeUnit.MILLISECONDS)
                        .continueWith(new Func1<Maybe<Boolean>, Maybe<Boolean>>() {
                            // Revert the result.
                            @Override
                            public Maybe<Boolean> apply(Maybe<Boolean> value) {
                                return value.isNothing() ? value : Maybe.just(!value.get());
                            }
                        }).continueWithWaiter();
                waiter.execute(schedulerPool.getScheduler(key));
                waiters[i] = waiter;
                Thread.sleep(10);
            }
            for (int i = 0; i < waiters.length; i++) {
                Assert.assertEquals(i % 2 != 0, waiters[i].getResult().get());
            }
            Assert.assertEquals(0, client.getRequestRecords().size());

            // Test when results don't come back in time.
            for (int i = 0; i < waiters.length; i++) {
                String key = Long.toHexString(random.nextLong());
                Waiter<Maybe<Boolean>> waiter = client.call(key, i, 5, TimeUnit.MILLISECONDS).continueWithWaiter();
                waiter.execute(schedulerPool.getScheduler(key));
                waiters[i] = waiter;
                Thread.sleep(10);
            }
            for (int i = 0; i < waiters.length; i++) {
                Assert.assertTrue(waiters[i].getResult().isNothing());
            }
            Assert.assertEquals(0, client.getRequestRecords().size());

            // Verify that all tasks for the same key are executed in the same thread.
            Thread.sleep(100);
            checkThreadInfo();
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
     * A Fake producer/consumer client.
     * @param <K>
     * @param <V1>
     * @param <V2>
     */
    public static class Client<K, V1, V2> {
        private final Func1<V1, V2> logic;
        private final ExecutorService executor;
        private final BlockingQueue<Pair<K, V1>> queue = new ArrayBlockingQueue<Pair<K, V1>>(100);

        public Client(Func1<V1, V2> logic) {
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
                            Thread.sleep(10);
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

    private static final HashMap<Object, List<Thread>> threadMap = new HashMap<Object, List<Thread>>();

    /**
     * For checking thread usages.
     * @param key
     * @param <K>
     */
    private static <K> void addThreadInfo(K key) {
        synchronized (threadMap) {
            List<Thread> threads = threadMap.get(key);
            if (threads == null) {
                threads = new ArrayList<Thread>();
                threadMap.put(key, threads);
            }
            threads.add(Thread.currentThread());
        }
    }

    /**
     * For checking thread usages.
     */
    private static void checkThreadInfo() {
        for (Map.Entry<Object, List<Thread>> entry: threadMap.entrySet()) {
            List<Thread> threads = entry.getValue();
            System.out.println(entry.getKey() + " -> " + threads);
            Thread thread0 = null;
            for (Thread thread: threads) {
                if (thread0 == null) {
                    thread0 = thread;
                } else {
                    Assert.assertSame(thread0, thread);
                }
            }
        }
    }

    /**
     * The Task-style async client.
     * @param <K>
     * @param <V1>
     * @param <V2>
     */
    public static class AsyncClient<K, V1, V2> {
        private final Client<K, V1, V2> client;
        private final ConcurrentHashMap<K, Context<Unit, V2>> requestRecords;
        private final Timer timer = new ExecutorTimer();

        public AsyncClient(Client<K, V1, V2> client) {
            this.client = client;
            requestRecords = new ConcurrentHashMap<K, Context<Unit, V2>>();
            client.start(new Client.Handler<K, V2>() {
                @Override
                public void onReceive(K key, V2 result) {
                    Context<Unit, V2> context = requestRecords.remove(key);
                    if (context != null) {
                        context.resume(result);
                    }
                }
            });
        }

        private Task<V2> call0(final K key, final V1 input) {
            return Task.create(new ContextAction<Unit, V2>() {
                @Override
                public void apply(Context<Unit, V2> context) {
                    addThreadInfo(key);
                    requestRecords.put(key, context);
                    client.send(key, input);
                }
            });
        }

        /**
         * Asynchronously call the remote service. We do not provide a version without timeout mechanism, which cannot
         * guarantee to do necessary cleanup.
         * @param key
         * @param input
         * @param timeout
         * @param unit
         * @return
         */
        public Task<Maybe<V2>> call(final K key, V1 input, long timeout, TimeUnit unit) {
            return timer.withTimeout(call0(key, input), timeout, unit)
                    .continueWith(new Func1<Maybe<V2>, Maybe<V2>>() {
                        @Override
                        public Maybe<V2> apply(Maybe<V2> value) {
                            // Cleanup.
                            addThreadInfo(key);
                            requestRecords.remove(key);
                            return value;
                        }
                    });
        }

        /**
         * For test only.
         * @return
         */
        public ConcurrentHashMap<K, Context<Unit, V2>> getRequestRecords() {
            return requestRecords;
        }
    }
}
