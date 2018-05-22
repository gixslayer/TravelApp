package rnd.travelapp.cache;

import java.util.Optional;

import rnd.travelapp.utils.Failable;

public interface Cache {
    <T> Optional<T> get(String key, Class<T> type);
    <T> Failable<T> fetch(String key, Class<T> type);

    default <T> T getOrDefault(String key, T defaultValue, Class<T> type) {
        return get(key, type).orElse(defaultValue);
    }

    default <T> Failable<T> getOrFetch(String key, Class<T> type) {
        return get(key, type).map(Failable::success).orElseGet(() -> fetch(key, type));
    }
}
