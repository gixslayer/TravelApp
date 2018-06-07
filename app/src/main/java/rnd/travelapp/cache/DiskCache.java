package rnd.travelapp.cache;

import android.util.Log;

import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

import rnd.travelapp.resources.Resource;
import rnd.travelapp.serialization.BinaryLoader;
import rnd.travelapp.serialization.BinaryLoaders;
import rnd.travelapp.serialization.JSONLoaders;
import rnd.travelapp.utils.Failable;
import rnd.travelapp.utils.StreamUtils;

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

    public void index() {
        meta.index();
    }

    public void verify() {
        meta.verify().ifSucceeded(entries -> entries.forEach(this::processValidatorEntry));
    }

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
                failReason -> Log.e("TRAVEL_APP", "File fetch from validator entry failed", failReason)
        );
    }

    private <T> Failable<T> load(File file, Class<T> type) {
        try(FileInputStream stream = new FileInputStream(file)) {
            String json = StreamUtils.toString(stream);

            return Failable.success(JSONLoaders.deserialize(json, type));
        } catch (IOException | JSONException e) {
            Log.e("CACHE_APP", "Failed to deserialize json file", e);

            return Failable.failure(e);
        }
    }

    private <T> Failable<Resource<T>> loadResource(File file, Class<? extends Resource<T>> type) {
        try(FileInputStream stream = new FileInputStream(file)) {
            BinaryLoader<T> loader = BinaryLoaders.get(type);

            T resource = loader.deserialize(stream);
            Resource<T> instance = loader.create(meta.getPath(file), resource);

            return Failable.success(instance);
        } catch (IOException e) {
            Log.e("CACHE_APP", "Failed to deserialize binary file", e);

            return Failable.failure(e);
        }
    }

    private Failable<File> fetchFile(String key) {
        return fetcher.fetch(key, meta.getFile(key)).processSafe(file -> {
            meta.addIfMissing(key, file);

            return file;
        });
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
