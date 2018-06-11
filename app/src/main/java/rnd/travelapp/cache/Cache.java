package rnd.travelapp.cache;

import java.util.Optional;

import rnd.travelapp.utils.Failable;

/**
 * Defines a cache interface for fetching models.
 */
public interface Cache {
    /**
     * Get the model if it is currently cached.
     * @param key the entry key
     * @param type the model type
     * @param <T> the model type
     * @return the model instance if it was cached, or an empty Optional otherwise
     */
    <T> Optional<T> get(String key, Class<T> type);

    /**
     * Fetch the model from the backing data store.
     * @param key the entry key
     * @param type the model type
     * @param <T> the model type
     * @return the model instance if it was fetched successfully, or the cause of the failure otherwise
     */
    <T> Failable<T> fetch(String key, Class<T> type);

    /**
     * Get the model if it is currently cached, or a default value otherwise.
     * @param key the entry key
     * @param defaultValue the default value if no cached value exists
     * @param type the model type
     * @param <T> the model type
     * @return the model instance if it was cached, or the default value otherwise
     */
    default <T> T getOrDefault(String key, T defaultValue, Class<T> type) {
        return get(key, type).orElse(defaultValue);
    }

    /**
     * Get the model if it is currently cached, or fetch it from the backing store otherwise.
     * @param key the entry key
     * @param type the model type
     * @param <T> the model type
     * @return the model instance it was obtained successfully, or the cause of the failure otherwise
     */
    default <T> Failable<T> getOrFetch(String key, Class<T> type) {
        return get(key, type).map(Failable::success).orElseGet(() -> fetch(key, type));
    }
}
