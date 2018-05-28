package rnd.travelapp.cache;

import android.content.Context;
import android.content.res.Resources;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import rnd.travelapp.R;
import rnd.travelapp.resources.Resource;
import rnd.travelapp.threading.Task;
import rnd.travelapp.threading.VoidTask;
import rnd.travelapp.utils.Failable;

public class AppCache {
    private static final Map<Context, AppCache> INSTANCES = new HashMap<>();

    private final DiskCache diskCache;
    private final MemoryCache memoryCache;
    private final ResourceMemoryCache resourceMemoryCache;

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
    }

    private AppCache initialize() {
        diskCache.index();

        return this;
    }

    private void doVerify() {

        diskCache.verify();
    }

    public VoidTask verify() {
        return Task.create(this::doVerify);
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
