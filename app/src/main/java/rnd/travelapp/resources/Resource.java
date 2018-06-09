package rnd.travelapp.resources;

import android.content.Context;

import java.util.Optional;
import java.util.function.Consumer;

import rnd.travelapp.cache.AppCache;
import rnd.travelapp.utils.Failable;

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

    public Class<T> getResourceType() {
        return type;
    }

    public Optional<T> get() {
        return Optional.ofNullable(resource);
    }

    public T getOrDefault(T defaultValue) {
        return resource != null ? resource : defaultValue;
    }

    public void getOrFetch(Context context, Consumer<Failable<T>> onCompletion) {
        if (resource != null) {
            onCompletion.accept(Failable.success(resource));
        }

        AppCache.getFor(context.getApplicationContext()).onCompletion(cache -> getOrFetch(cache, onCompletion));
    }

    public void getOrFetch(Context context, Consumer<T> onSuccess, Consumer<Throwable> onFailure) {
        getOrFetch(context, result -> result.consume(onSuccess, onFailure));
    }

    public void getOrFetch(AppCache cache, Consumer<Failable<T>> onCompletion) {
        if (resource != null) {
            onCompletion.accept(Failable.success(resource));
        }

        cache.getResourceOrFetch(path, classType).onCompletion(result -> {
            onCompletion.accept(result.processSafe(r -> resource = r.resource));
        });
    }

    public void getOrFetch(AppCache cache, Consumer<T> onSuccess, Consumer<Throwable> onFailure) {
        getOrFetch(cache, result -> result.consume(onSuccess, onFailure));
    }

    public abstract int getSize();
}
