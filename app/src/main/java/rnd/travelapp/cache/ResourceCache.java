package rnd.travelapp.cache;

import java.util.Optional;

import rnd.travelapp.resources.Resource;
import rnd.travelapp.utils.Failable;

/**
 * Defines a cache interface for fetching wrapped resources.
 */
public interface ResourceCache {
    /**
     * Get the wrapped resource if it is currently cached.
     * @param key the entry key
     * @param type the wrapped resource type
     * @param <T> the unwrapped resource type
     * @return the wrapped resource instance if it was cached, or an empty Optional otherwise
     */
    <T> Optional<Resource<T>> getResource(String key, Class<? extends Resource<T>> type);

    /**
     * Fetch the wrapper resource from the backing data store.
     * @param key the entry key
     * @param type the wrapped resource type
     * @param <T> the unwrapped resource type
     * @return the wrapped resource instance if it was fetched successfully, or the cause of the failure otherwise
     */
    <T> Failable<Resource<T>> fetchResource(String key, Class<? extends Resource<T>> type);

    /**
     * Get the wrapped resource if it is currently cached, or a default value otherwise.
     * @param key the entry key
     * @param defaultValue the default value if no cached value exists
     * @param type the wrapped resource type
     * @param <T> the unwrapped resource type
     * @return the wrapped resource instance if it was cached, or the default value otherwise
     */
    default <T> Resource<T> getResourceOrDefault(String key, Resource<T> defaultValue, Class<? extends Resource<T>> type) {
        return getResource(key, type).orElse(defaultValue);
    }

    /**
     * Get the wrapped resource if it is currently cached, or fetch it from the backing store otherwise.
     * @param key the entry key
     * @param type the wrapped resource type
     * @param <T> the unwrapped resource type
     * @return the wrapped resource instance it was obtained successfully, or the cause of the failure otherwise
     */
    default <T> Failable<Resource<T>> getResourceOrFetch(String key, Class<? extends Resource<T>> type) {
        return getResource(key, type).map(Failable::success).orElseGet(() -> fetchResource(key, type));
    }
}
