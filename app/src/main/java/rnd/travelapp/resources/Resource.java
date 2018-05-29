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

        AppCache.getFor(context).onCompletion(cache ->
                cache.getResourceOrFetch(path, classType).onCompletion(result ->
                        result.consume(r -> {
                            resource = r.resource;

                            onCompletion.accept(Failable.success(resource));
                        }, cause -> {
                            onCompletion.accept(Failable.failure(cause));
                        })));
    }

    public abstract int getSize();
}
