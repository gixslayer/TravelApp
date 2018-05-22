package rnd.travelapp.cache;

import java.util.Optional;

import rnd.travelapp.resources.Resource;
import rnd.travelapp.utils.Failable;

public class AppCache {
    public static <T> Optional<T> get(String key, Class<T> type) {
        return null;
    }

    public static <T> T getOrDefault(String key, T defaultValue, Class<T> type) {
        return null;
    }

    public static <T> Failable<T> getOrFetch(String key, Class<T> type) {
        return null;
    }

    public static <T> Optional<Resource<T>> getResource(String key, Class<? extends Resource<T>> type) {
        return null;
    }

    public static <T> Resource<T> getResourceOrDefault(String key, Resource<T> defaultValue, Class<? extends Resource<T>> type) {
        return null;
    }

    public static <T> Failable<Resource<T>> getResourceOrFetch(String key, Class<? extends Resource<T>> type) {
        return null;
    }
}
