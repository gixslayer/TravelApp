package rnd.travelapp.cache;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import rnd.travelapp.resources.Resource;
import rnd.travelapp.serialization.BinaryLoader;
import rnd.travelapp.serialization.BinaryLoaders;
import rnd.travelapp.serialization.JSONLoaders;
import rnd.travelapp.utils.CollectionUtils;
import rnd.travelapp.utils.Failable;
import rnd.travelapp.utils.StreamUtils;

public class DiskCache implements Cache, ResourceCache {
    private final Map<String, DiskCacheEntry> entries;
    private final Fetcher fetcher;
    private final File root;
    private final File meta;
    private String knownLastModified;

    public DiskCache(Fetcher fetcher, File directory) {
        if(!directory.exists()) {
            throw new IllegalArgumentException("directory does not exist");
        } else if(!directory.isDirectory()) {
            throw new IllegalArgumentException("directory is not a directory");
        }

        this.entries = new HashMap<>();
        this.fetcher = fetcher;
        this.root = new File(directory, "cache");
        this.meta = new File(directory, "cache-meta.json");
        this.knownLastModified = "";
    }

    public void index() {
        synchronized (entries) {
            if (meta.exists()) {
                loadMeta();
            }

            indexDirectory(root);

            // Remove meta entries that no longer exists.
            // TODO: Can this for each remove while streaming, or does this cause an exception?
            entries.values().stream()
                    .filter(e -> e.getFile() != null)
                    .map(DiskCacheEntry::getPath)
                    .forEach(entries::remove);

            saveMeta();
        }
    }

    private void loadMeta() {
        try(FileInputStream stream = new FileInputStream(meta)) {
            String json = StreamUtils.toString(stream);
            JSONObject metaObject = new JSONObject(json);

            knownLastModified = metaObject.getString("knownLastModified");
            JSONArray metaFiles = metaObject.getJSONArray("files");

            for(int i = 0; i < metaFiles.length(); ++i) {
                JSONObject metaEntryObject = metaFiles.getJSONObject(i);
                DiskCacheEntry metaEntry = DiskCacheEntry.fromMeta(metaEntryObject);

                entries.put(metaEntry.getPath(), metaEntry);
            }
        } catch (IOException | JSONException e) {
            Log.e("CACHE_APP", "Error loading cache meta file", e);
        }
    }

    private void saveMeta() {
        JSONObject metaObject = new JSONObject();
        JSONArray metaFiles = new JSONArray();

        try {
            for (DiskCacheEntry entry : entries.values()) {
                metaFiles.put(entry.toMeta());
            }

            metaObject.put("knownLastModified", knownLastModified);
            metaObject.put("files", metaFiles);

            try (FileOutputStream stream = new FileOutputStream(meta)) {
                String json = metaObject.toString(2);
                byte[] data = json.getBytes("UTF-8");

                stream.write(data);
            } catch (IOException e) {
                Log.e("CACHE_APP", "Error writing cache meta file", e);
            }
        } catch (JSONException e) {
            Log.e("CACHE_APP", "Error saving cache meta file", e);
        }
    }

    private void indexDirectory(File directory) {
        File[] files = directory.listFiles(File::isFile);
        File[] directories = directory.listFiles(File::isDirectory);

        CollectionUtils.forEach(files, this::indexFile);
        CollectionUtils.forEach(directories, this::indexDirectory);
    }

    private void indexFile(File file) {
        String path = getPath(file);
        DiskCacheEntry metaEntry = entries.get(path);

        if(metaEntry != null && metaEntry.getFile().lastModified() == file.lastModified()) {
            entries.put(path, DiskCacheEntry.update(metaEntry, file));
        } else {
            entries.put(path, DiskCacheEntry.fromFile(file, path));
        }
    }

    private File getFile(String key) {
        File file = root;

        for(String segment : key.split("/")) {
            file = new File(file, segment);
        }

        return file;
    }

    private String getPath(File file) {
        StringBuilder sb = new StringBuilder();

        for(File currentFile = file; !currentFile.equals(root); currentFile = currentFile.getParentFile()) {
            sb.insert(0, file.getName() + "/");
        }

        // NOTE: Assumes file is a child of root (and not equal to root).
        return sb.substring(0, sb.length() - 1);
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
            Resource<T> instance = loader.create(getPath(file), resource);

            return Failable.success(instance);
        } catch (IOException e) {
            Log.e("CACHE_APP", "Failed to deserialize binary file", e);

            return Failable.failure(e);
        }
    }

    private Failable<File> fetchFile(String key) {
        return fetcher.fetch(key, getFile(key)).processSafe(file -> {
            synchronized (entries) {
                entries.putIfAbsent(key, DiskCacheEntry.fromFile(file, key));

                // TODO: Is this too expensive?
                saveMeta();
            }

            return file;
        });
    }

    private Optional<DiskCacheEntry> getEntry(String key) {
        synchronized (entries) {
            return Optional.ofNullable(entries.get(key));
        }
    }

    @Override
    public <T> Optional<T> get(String key, Class<T> type) {
        return getEntry(key).map(e -> load(e.getFile(), type).orOnFailure(null));
    }

    @Override
    public <T> Failable<T> fetch(String key, Class<T> type) {
        return fetchFile(key).process(file -> load(file, type));
    }

    @Override
    public <T> Optional<Resource<T>> getResource(String key, Class<? extends Resource<T>> type) {
        return getEntry(key).map(e -> loadResource(e.getFile(), type).orOnFailure(null));
    }

    @Override
    public <T> Failable<Resource<T>> fetchResource(String key, Class<? extends Resource<T>> type) {
        return fetchFile(key).process(file -> loadResource(file, type));
    }
}
