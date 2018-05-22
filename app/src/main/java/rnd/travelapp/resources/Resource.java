package rnd.travelapp.resources;

import java.util.Optional;

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

    public Failable<T> getOrFetch() {
        if(resource != null) {
            return Failable.success(resource);
        }

        return AppCache.getResourceOrFetch(path, classType).processSafe(r -> {
            resource = r.resource;

           return resource;
        });
    }

    public abstract int getSize();
}
