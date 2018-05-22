package rnd.travelapp.cache;

import java.util.Optional;

import rnd.travelapp.resources.Resource;
import rnd.travelapp.utils.Failable;

public interface ResourceCache {
    <T> Optional<Resource<T>> getResource(String key, Class<? extends Resource<T>> type);
    <T> Failable<Resource<T>> fetchResource(String key, Class<? extends Resource<T>> type);

    default <T> Resource<T> getResourceOrDefault(String key, Resource<T> defaultValue, Class<? extends Resource<T>> type) {
        return getResource(key, type).orElse(defaultValue);
    }

    default <T> Failable<Resource<T>> getResourceOrFetch(String key, Class<? extends Resource<T>> type) {
        return getResource(key, type).map(Failable::success).orElseGet(() -> fetchResource(key, type));
    }
}
