package rnd.travelapp.resources;

import android.content.Context;

import java.util.Optional;
import java.util.function.Consumer;

import rnd.travelapp.cache.AppCache;
import rnd.travelapp.utils.Failable;

/**
 * Represents a resource wrapper which can fetch a resource from the cache when required.
 * @param <T> the type of the resource
 */
public abstract class Resource<T> {
    private final Class<T> type;
    private final Class<? extends Resource<T>> classType;
    private final String path;
    protected T resource;

    public Resource(Class<T> type, Class<? extends Resource<T>> classType, String path) {
        this(type, classType, path, null);
    }

    public Resource(Class<T> type, Class<? extends Resource<T>> classType, String path, T resource) {
        this.type = type;
        this.classType = classType;
        this.path = path;
        this.resource = resource;
    }

    /**
     * Returns the type of the resource, which is required as the generic type T is lost at runtime due to type erasure.
     * @return the type of T
     */
    public Class<T> getResourceType() {
        return type;
    }

    /**
     * Returns the current resource instance if one exists.
     * @return The current resource of this wrapper, or an empty Optional if no resource exists.
     */
    public Optional<T> get() {
        return Optional.ofNullable(resource);
    }

    /**
     * Returns the current resource instance if one exists, or a default value otherwise.
     * @param defaultValue the default value to return if no resource exists
     * @return The current resource of this wrapper, or the default value if no resource exists.
     */
    public T getOrDefault(T defaultValue) {
        return resource != null ? resource : defaultValue;
    }

    /**
     * Invokes the given onCompletion callback with the resource, which is fetched in a background thread if it does not exist.
     * @param context the context to get the AppCache from
     * @param onCompletion the method to invoke on the UI thread once the async task is completed
     */
    public void getOrFetch(Context context, Consumer<Failable<T>> onCompletion) {
        if (resource != null) {
            onCompletion.accept(Failable.success(resource));
        }

        AppCache.getFor(context.getApplicationContext()).onCompletion(cache -> getOrFetch(cache, onCompletion));
    }

    /**
     * Invokes the given onCompletion callback with the resource, which is fetched in a background thread if it does not exist.
     * @param context the context to get the AppCache from
     * @param onSuccess the method to invoke on the UI thread once the async task is successfully completed
     * @param onFailure the method to invoke on the UI thread if the fetching failed
     */
    public void getOrFetch(Context context, Consumer<T> onSuccess, Consumer<Throwable> onFailure) {
        getOrFetch(context, result -> result.consume(onSuccess, onFailure));
    }

    /**
     * Invokes the given onCompletion callback with the resource, which is fetched in a background thread if it does not exist.
     * @param cache the AppCache to fetch from
     * @param onCompletion the method to invoke on the UI thread once the async task is completed
     */
    public void getOrFetch(AppCache cache, Consumer<Failable<T>> onCompletion) {
        if (resource != null) {
            onCompletion.accept(Failable.success(resource));
        }

        cache.getResourceOrFetch(path, classType).onCompletion(result -> {
            onCompletion.accept(result.processSafe(r -> resource = r.resource));
        });
    }

    /**
     * Invokes the given onCompletion callback with the resource, which is fetched in a background thread if it does not exist.
     * @param cache the AppCache to fetch from
     * @param onSuccess the method to invoke on the UI thread once the async task is successfully completed
     * @param onFailure the method to invoke on the UI thread if the fetching failed
     */
    public void getOrFetch(AppCache cache, Consumer<T> onSuccess, Consumer<Throwable> onFailure) {
        getOrFetch(cache, result -> result.consume(onSuccess, onFailure));
    }

    /**
     * Returns the size of the resource, which is used to determine memory consumption in the resource memory cache.
     * @return the current size of the resource in bytes
     */
    public abstract int getSize();
}
