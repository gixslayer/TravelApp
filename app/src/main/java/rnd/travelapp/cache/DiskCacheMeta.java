package rnd.travelapp.cache;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import rnd.travelapp.utils.CollectionUtils;
import rnd.travelapp.utils.Failable;
import rnd.travelapp.utils.StreamUtils;

/**
 * Handles metadata for a DiskCache.
 */
public class DiskCacheMeta {
    private static final List<ValidatorEntry> EMPTY_VALIDATOR_LIST = new ArrayList<>();

    private final Map<String, DiskCacheEntry> entries;
    private final Validator validator;
    private final File meta;
    private final File root;
    private volatile String knownLastModified;

    public DiskCacheMeta(Validator validator, File meta, File root) {
        this.entries = new HashMap<>();
        this.validator = validator;
        this.meta = meta;
        this.root = root;
    }

    /**
     * Index all the files currently on-disk and in the saved on-disk meta file if one exists.
     */
    public void index() {
        synchronized (entries) {
            if (meta.exists()) {
                loadMeta();
            }

            indexDirectory(root);

            // Remove meta entries that no longer exists. Store in copy before removing to avoid
            // concurrent modification.
            List<String> removedEntries = entries.values().stream()
                    .filter(e -> e.getFile() == null)
                    .map(DiskCacheEntry::getPath)
                    .collect(Collectors.toList());
            removedEntries.forEach(entries::remove);

            saveMeta();
        }
    }

    /**
     * Verify the integrity of the on-disk files, returning a list of mutations that should occur to synchronize the cache from the backing store.
     * @return the mutations to occur upon success, or the failure cause otherwise.
     */
    public Failable<List<ValidatorEntry>> verify() {
        return validator.getLastModified().process(lastModified -> {
           if(lastModified.equals(knownLastModified)) {
               return Failable.success(EMPTY_VALIDATOR_LIST);
           } else {
               knownLastModified = lastModified;

               saveMeta();

               return validator.validate(entries.values());
           }
        });
    }

    /**
     * Add a new entry if it is currently missing.
     * @param key the entry key
     * @param file the on-disk file the entry is stored to
     * @return the on-disk file
     */
    public File addIfMissing(String key, File file) {
        synchronized (entries) {
            entries.putIfAbsent(key, DiskCacheEntry.fromFile(file, key));
        }

        return file;
    }

    /**
     * Adds or replaces the entry.
     * @param key the entry key
     * @param file the on-disk file the entry is stored to
     * @param checksum the checksum of the on-disk file
     */
    public void addOrUpdate(String key, File file, String checksum) {
        synchronized (entries) {
            entries.put(key, DiskCacheEntry.fromFile(file, key, checksum));
        }
    }

    /**
     * Removes an entry.
     * @param key the entry key
     */
    public void remove(String key) {
        synchronized (entries) {
            entries.remove(key);
        }
    }

    /**
     * Gets an entry if one exists for the given key.
     * @param key the entry key
     * @return the entry if one exists, or an empty Optional otherwise
     */
    public Optional<DiskCacheEntry> getEntry(String key) {
        synchronized (entries) {
            return Optional.ofNullable(entries.get(key));
        }
    }

    /**
     * Get the on-disk File instance for the given entry key.
     * @param key the entry key
     * @return the File instance, which might not exist
     */
    public File getFile(String key) {
        File file = root;

        for(String segment : key.split("/")) {
            file = new File(file, segment);
        }

        return file;
    }

    /**
     * Get the file path for the given entry key.
     * @param file the entry key
     * @return the file path
     */
    public String getPath(File file) {
        StringBuilder sb = new StringBuilder();

        for(File currentFile = file; !currentFile.equals(root); currentFile = currentFile.getParentFile()) {
            sb.insert(0, currentFile.getName() + "/");
        }

        // NOTE: Assumes file is a child of root (and not equal to root).
        return sb.substring(0, sb.length() - 1);
    }

    /**
     * Save any changes made to the cache to the on-disk meta file.
     */
    public void saveChanges() {
        saveMeta();
    }

    private void loadMeta() {
        try {
            String json = loadFromMetaFile();
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

            metaObject.put("knownLastModified", knownLastModified == null ? "" : knownLastModified);
            metaObject.put("files", metaFiles);

            String json = metaObject.toString(2);

            try {
                writeToMetaFile(json);
            } catch (IOException e) {
                Log.e("CACHE_APP", "Error writing cache meta file", e);
            }
        } catch (JSONException e) {
            Log.e("CACHE_APP", "Error saving cache meta file", e);
        }
    }

    private String loadFromMetaFile() throws IOException {
        synchronized (meta) {
            try (FileInputStream stream = new FileInputStream(meta)) {
                return StreamUtils.toString(stream);
            }
        }
    }

    private void writeToMetaFile(String string) throws IOException {
        byte[] data = string.getBytes(StandardCharsets.UTF_8);

        synchronized (meta) {
            try (FileOutputStream stream = new FileOutputStream(meta)) {
                stream.write(data);
            }
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

        if(metaEntry != null && metaEntry.getAcquired().getTime() == file.lastModified()) {
            entries.put(path, DiskCacheEntry.update(metaEntry, file));
        } else {
            entries.put(path, DiskCacheEntry.fromFile(file, path));
        }
    }
}
