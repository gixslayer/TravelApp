package rnd.travelapp.cache;

import android.content.Context;
import android.content.res.Resources;

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
import rnd.travelapp.threading.Task;
import rnd.travelapp.threading.VoidTask;
import rnd.travelapp.utils.Failable;

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

    private <T> Failable<Map<String, T>> awaitTasks(Map<String, Future<Failable<T>>> tasks) {
        Map<String, T> results = new HashMap<>();

        for(String key : tasks.keySet()) {
            Future<Failable<T>> task = tasks.get(key);

            try {
                Failable<T> result = task.get();

                if(result.hasFailed()) {
                    return Failable.failure(result.getCause());
                } else {
                    results.put(key, result.get());
                }
            } catch (InterruptedException | ExecutionException e) {
                return Failable.failure(e);
            }
        }

        return Failable.success(results);
    }

    private <T> Failable<Map<String, T>> doGetOrFetchFromList(ModelList list, Class<T> type) {
        Map<String, Future<Failable<T>>> tasks = new HashMap<>();
        ExecutorService executor = Executors.newCachedThreadPool();

        list.forEach(key -> tasks.put(key, executor.submit(() -> memoryCache.getOrFetch(key, type))));

        executor.shutdown();

        return awaitTasks(tasks);
    }

    public VoidTask verify() {
        return Task.create(this::doVerify);
    }

    public void saveChanges() {
        Task.run(diskCache::saveChanges);
    }

    public <T> Task<Optional<T>> get(String key, Class<T> type) {
        return Task.create(() -> memoryCache.get(key, type));
    }

    public <T> Task<T> getOrDefault(String key, T defaultValue, Class<T> type) {
        return Task.create(() -> memoryCache.getOrDefault(key, defaultValue, type));
    }

    public <T> Task<Failable<T>> getOrFetch(String key, Class<T> type) {
        return Task.create(() -> memoryCache.getOrFetch(key, type));
    }

    public <T> Task<Failable<Map<String, T>>> getOrFetchList(String listKey, Class<T> type) {
        return Task.create(() -> memoryCache.getOrFetch(listKey, ModelList.class).process(list -> doGetOrFetchFromList(list, type)));
    }

    public <T> Task<Failable<Map<String, T>>> getOrFetchFromList(ModelList list, Class<T> type) {
        return Task.create(() -> doGetOrFetchFromList(list, type));
    }

    public <T> Task<Optional<Resource<T>>> getResource(String key, Class<? extends Resource<T>> type) {
        return Task.create(() -> resourceMemoryCache.getResource(key, type));
    }

    public <T> Task<Resource<T>> getResourceOrDefault(String key, Resource<T> defaultValue, Class<? extends Resource<T>> type) {
        return Task.create(() -> resourceMemoryCache.getResourceOrDefault(key, defaultValue, type));
    }

    public <T> Task<Failable<Resource<T>>> getResourceOrFetch(String key, Class<? extends Resource<T>> type) {
        return Task.create(() -> resourceMemoryCache.getResourceOrFetch(key, type));
    }

    public static Task<AppCache> getFor(Context context) {
        AppCache instance;

        synchronized (INSTANCES) {
            instance = INSTANCES.computeIfAbsent(context, AppCache::new);
        }

        return Task.create(instance::initialize);
    }
}
