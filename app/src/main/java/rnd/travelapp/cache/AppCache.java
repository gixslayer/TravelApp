package rnd.travelapp.cache;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

import rnd.travelapp.R;
import rnd.travelapp.models.ModelList;
import rnd.travelapp.resources.Resource;
import rnd.travelapp.threading.FailableTask;
import rnd.travelapp.threading.Task;
import rnd.travelapp.threading.VoidTask;
import rnd.travelapp.utils.Failable;

/**
 * The central AppCache through which all cache interaction takes place.
 */
public class AppCache {
    private static final Map<Context, AppCache> INSTANCES = new HashMap<>();

    private final DiskCache diskCache;
    private final MemoryCache memoryCache;
    private final ResourceMemoryCache resourceMemoryCache;
    private final ReentrantLock initializeLock;
    private boolean initialized;

    private AppCache(Context context) {
        Resources resources = context.getResources();
        String serverHost = resources.getString(R.string.server_host);
        int serverPort = resources.getInteger(R.integer.server_port);
        int maxMemorySize = resources.getInteger(R.integer.max_memory_cache_size);
        Fetcher fetcher = new HttpFetcher(serverHost, serverPort);
        Validator validator = new HttpValidator(serverHost, serverPort);

        this.diskCache = new DiskCache(fetcher, validator, context.getCacheDir());
        this.memoryCache = new MemoryCache(diskCache);
        this.resourceMemoryCache = new ResourceMemoryCache(diskCache, maxMemorySize);
        this.initializeLock = new ReentrantLock();
        this.initialized = false;
    }

    /**
     * Initializes the cache if required in a thread-safe manner.
     * @return this instance
     */
    private AppCache initialize() {
        initializeLock.lock();

        try {
            if(!initialized) {
                diskCache.index();
                initialized = true;
            }
        } finally {
            initializeLock.unlock();
        }

        return this;
    }

    private void doVerify() {
        diskCache.verify();
    }

    /**
     * Await all tasks until their results are available, removing failed tasks, and returning the results.
     * @param tasks the tasks to await
     * @param <T> the task result type
     * @return a list of succeeded task results, or the failure cause otherwise
     */
    private <T> Failable<Map<String, T>> awaitTasks(Map<String, Future<Failable<T>>> tasks) {
        Map<String, T> results = new HashMap<>();

        for(String key : tasks.keySet()) {
            Future<Failable<T>> task = tasks.get(key);

            try {
                task.get().consume(
                        result -> results.put(key, result),
                        cause -> Log.e("TRAVEL_APP", String.format("Fetch task for file %s failed", key), cause)
                );
            } catch (InterruptedException | ExecutionException e) {
                return Failable.failure(e);
            }
        }

        return Failable.success(results);
    }

    /**
     * Fetch all models from the given list.
     * @param list the model list to fetch from
     * @param type the model type
     * @param <T> the model type
     * @return the fetched model, model path pairs, or the failure cause otherwise
     */
    private <T> Failable<Map<String, T>> doGetOrFetchFromList(ModelList list, Class<T> type) {
        Map<String, Future<Failable<T>>> tasks = new HashMap<>();
        ExecutorService executor = Executors.newCachedThreadPool();

        list.forEach(key -> tasks.put(key, executor.submit(() -> memoryCache.getOrFetch(key, type))));

        executor.shutdown();

        return awaitTasks(tasks);
    }

    /**
     * Verifies the integrity of this cache.
     * @return the verify task
     */
    public VoidTask verify() {
        return Task.create(this::doVerify);
    }

    /**
     * Saves any changes made to this cache in a background task.
     */
    public void saveChanges() {
        Task.run(diskCache::saveChanges);
    }

    /**
     * Get the model if it is currently cached.
     * @param key the entry key
     * @param type the model type
     * @param <T> the model type
     * @return an async task that contains the model instance if it was cached, or an empty Optional otherwise upon completion
     */
    public <T> Task<Optional<T>> get(String key, Class<T> type) {
        return Task.create(() -> memoryCache.get(key, type));
    }

    /**
     * Get the model if it is currently cached, or a default value otherwise.
     * @param key the entry key
     * @param defaultValue the default value if no cached value exists
     * @param type the model type
     * @param <T> the model type
     * @return an async task that contains the model instance if it was cached, or the default value otherwise upon completion
     */
    public <T> Task<T> getOrDefault(String key, T defaultValue, Class<T> type) {
        return Task.create(() -> memoryCache.getOrDefault(key, defaultValue, type));
    }

    /**
     * Get the model if it is currently cached, or fetch it from the backing store otherwise.
     * @param key the entry key
     * @param type the model type
     * @param <T> the model type
     * @return an async task that contains the model instance it was obtained successfully, or the cause of the failure otherwise upon completion
     */
    public <T> FailableTask<T> getOrFetch(String key, Class<T> type) {
        return Task.createFailable(() -> memoryCache.getOrFetch(key, type));
    }

    /**
     * Fetch all models from the list with the given key.
     * @param listKey the entry key of the model list to fetch from
     * @param type the model type
     * @param <T> the model type
     * @return an async task that contains the fetched model, model path pairs, or the failure cause otherwise upon completion
     */
    public <T> FailableTask<Map<String, T>> getOrFetchList(String listKey, Class<T> type) {
        return Task.createFailable(() -> memoryCache.getOrFetch(listKey, ModelList.class).process(list -> doGetOrFetchFromList(list, type)));
    }

    /**
     * Fetch all models from the given list.
     * @param list the model list to fetch from
     * @param type the model type
     * @param <T> the model type
     * @return an async task that contains the fetched model, model path pairs, or the failure cause otherwise upon completion
     */
    public <T> FailableTask<Map<String, T>> getOrFetchFromList(ModelList list, Class<T> type) {
        return Task.createFailable(() -> doGetOrFetchFromList(list, type));
    }

    /**
     * Get the wrapped resource if it is currently cached.
     * @param key the entry key
     * @param type the wrapped resource type
     * @param <T> the unwrapped resource type
     * @return an async task that contains the wrapped resource instance if it was cached, or an empty Optional otherwise upon completion
     */
    public <T> Task<Optional<Resource<T>>> getResource(String key, Class<? extends Resource<T>> type) {
        return Task.create(() -> resourceMemoryCache.getResource(key, type));
    }

    /**
     * Get the wrapped resource if it is currently cached, or a default value otherwise.
     * @param key the entry key
     * @param defaultValue the default value if no cached value exists
     * @param type the wrapped resource type
     * @param <T> the unwrapped resource type
     * @return an async task that contains the wrapped resource instance if it was cached, or the default value otherwise upon completion
     */
    public <T> Task<Resource<T>> getResourceOrDefault(String key, Resource<T> defaultValue, Class<? extends Resource<T>> type) {
        return Task.create(() -> resourceMemoryCache.getResourceOrDefault(key, defaultValue, type));
    }

    /**
     * Get the wrapped resource if it is currently cached, or fetch it from the backing store otherwise.
     * @param key the entry key
     * @param type the wrapped resource type
     * @param <T> the unwrapped resource type
     * @return an async task that contains the wrapped resource instance it was obtained successfully, or the cause of the failure otherwise upon completion
     */
    public <T> FailableTask<Resource<T>> getResourceOrFetch(String key, Class<? extends Resource<T>> type) {
        return Task.createFailable(() -> resourceMemoryCache.getResourceOrFetch(key, type));
    }

    /**
     * Gets the AppCache for the given context.
     * @param context the context
     * @return an async task that contains the initialized AppCache upon completion
     */
    public static Task<AppCache> getFor(Context context) {
        AppCache instance;

        synchronized (INSTANCES) {
            instance = INSTANCES.computeIfAbsent(context, AppCache::new);
        }

        return Task.create(instance::initialize);
    }
}
