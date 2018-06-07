package rnd.travelapp.cache;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

import rnd.travelapp.resources.Resource;
import rnd.travelapp.utils.Failable;

public class ResourceMemoryCache implements ResourceCache {
    private final Map<String, Resource> resources;
    private final Queue<String> evictionQueue;
    private final ResourceCache fallback;
    private final int maxSize;
    private int currentSize;

    public ResourceMemoryCache(ResourceCache fallback, int maxsize) {
        this.resources = new HashMap<>();
        this.evictionQueue = new ArrayDeque<>();
        this.fallback = fallback;
        this.maxSize = maxsize;
        this.currentSize = 0;
    }

    @Override
    public <T> Optional<Resource<T>> getResource(String key, Class<? extends Resource<T>> type) {
        Resource resource = resources.get(key);

        if(resource != null) {
            Class<?> resourceType = resource.getClass(); //getResourceType();

            if(!resourceType.equals(type)) {
                throw new IllegalArgumentException(String.format(
                        "Resource '%s' of type '%s' cannot be converted to type '%s'",
                        key, resourceType, type));
            }

            // Bump to the back of the eviction queue as the element was just accessed.
            evictionQueue.remove(key);
            evictionQueue.add(key);

            //noinspection unchecked
            return Optional.of((Resource<T>)resource);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public <T> Failable<Resource<T>> fetchResource(String key, Class<? extends Resource<T>> type) {
        return fallback.getResourceOrFetch(key, type).processSafe(resource -> {
            resources.put(key, resource);
            evictionQueue.add(key);
            currentSize += resource.getSize();

            while (currentSize > maxSize && !evictionQueue.isEmpty()) {
                evict(evictionQueue.remove());
            }

            return resource;
        });
    }

    private void evict(String key) {
        Resource resource = resources.remove(key);

        currentSize -= resource.getSize();
    }
}
