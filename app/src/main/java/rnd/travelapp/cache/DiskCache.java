package rnd.travelapp.cache;

import android.util.Log;

import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

import rnd.travelapp.resources.Resource;
import rnd.travelapp.serialization.BinaryLoaders;
import rnd.travelapp.serialization.JSONLoaders;
import rnd.travelapp.utils.Failable;
import rnd.travelapp.utils.StreamUtils;

/**
 * Represents an on-disk cache for both models and wrapped resources.
 */
public class DiskCache implements Cache, ResourceCache {
    private final DiskCacheMeta meta;
    private final Fetcher fetcher;

    public DiskCache(Fetcher fetcher, Validator validator, File directory) {
        if(!directory.exists()) {
            throw new IllegalArgumentException("directory does not exist");
        } else if(!directory.isDirectory()) {
            throw new IllegalArgumentException("directory is not a directory");
        }

        File root = new File(directory, "cache");
        File meta = new File(directory, "cache-meta.json");

        this.meta = new DiskCacheMeta(validator, meta, root);
        this.fetcher = fetcher;
    }

    /**
     * Index all the files currently on-disk.
     */
    public void index() {
        meta.index();
    }

    /**
     * Verify the integrity of the on-disk files.
     */
    public void verify() {
        meta.verify().ifSucceeded(entries -> entries.forEach(this::processValidatorEntry));
    }

    /**
     * Save any changes made to the cache.
     */
    public void saveChanges() {
        meta.saveChanges();
    }

    private void processValidatorEntry(ValidatorEntry entry) {
        String path = entry.getFile();
        File file = meta.getFile(path);

        if(entry.getStatus() == ValidatorEntry.Status.Removed) {
            deleteFile(path, file);
        } else {
            fetchFile(path, file, entry.getChecksum());
        }
    }

    private void deleteFile(String path, File file) {
        if(file.exists() && file.isFile()) {
            if(file.delete()) {
                meta.remove(path);
            } else {
                Log.e("TRAVEL_APP", "File deletion failed");
            }
        }
    }

    private void fetchFile(String path, File destination, String checksum) {
        fetcher.fetch(path, destination).consume(
                file -> meta.addOrUpdate(path, destination, checksum),
                cause -> Log.e("TRAVEL_APP", "File fetch from validator entry failed", cause)
        );
    }

    private Failable<File> fetchFile(String key) {
        return fetcher.fetch(key, meta.getFile(key)).processSafe(file -> meta.addIfMissing(key, file));
    }

    private <T> Failable<T> load(File file, Class<T> type) {
        try(FileInputStream stream = new FileInputStream(file)) {
            String json = StreamUtils.toString(stream);
            T instance = JSONLoaders.deserialize(json, type);

            return Failable.success(instance);
        } catch (IOException | JSONException e) {
            Log.e("CACHE_APP", "Failed to deserialize json file", e);

            return Failable.failure(e);
        }
    }

    private <T> Failable<Resource<T>> loadResource(File file, Class<? extends Resource<T>> type) {
        try(FileInputStream stream = new FileInputStream(file)) {
            T resource = BinaryLoaders.deserialize(stream, type);
            Resource<T> instance = BinaryLoaders.get(type).create(meta.getPath(file), resource);

            return Failable.success(instance);
        } catch (IOException e) {
            Log.e("CACHE_APP", "Failed to deserialize binary file", e);

            return Failable.failure(e);
        }
    }

    @Override
    public <T> Optional<T> get(String key, Class<T> type) {
        return meta.getEntry(key).map(e -> load(e.getFile(), type).orOnFailure(null));
    }

    @Override
    public <T> Failable<T> fetch(String key, Class<T> type) {
        return fetchFile(key).process(file -> load(file, type));
    }

    @Override
    public <T> Optional<Resource<T>> getResource(String key, Class<? extends Resource<T>> type) {
        return meta.getEntry(key).map(e -> loadResource(e.getFile(), type).orOnFailure(null));
    }

    @Override
    public <T> Failable<Resource<T>> fetchResource(String key, Class<? extends Resource<T>> type) {
        return fetchFile(key).process(file -> loadResource(file, type));
    }
}
