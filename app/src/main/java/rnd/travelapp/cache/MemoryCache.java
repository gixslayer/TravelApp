package rnd.travelapp.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import rnd.travelapp.utils.Failable;

public class MemoryCache implements Cache {
    private final Map<String, Object> entries;
    private final Cache fallback;

    public MemoryCache(Cache fallback) {
        this.entries = new HashMap<>();
        this.fallback = fallback;
    }

    @Override
    public <T> Optional<T> get(String key, Class<T> type) {
        Object entry = entries.get(key);

        if(entry != null) {
            Class<?> entryType = entry.getClass();

            if(!entryType.equals(type)) {
                throw new IllegalArgumentException(String.format(
                        "Entry '%s' of type '%s' cannot be converted to type '%s'",
                        key, entryType, type));
            }

            //noinspection unchecked
            return Optional.of((T)entry);
        }

        return Optional.empty();
    }

    @Override
    public <T> Failable<T> fetch(String key, Class<T> type) {
        return fallback.getOrFetch(key, type);
    }
}
