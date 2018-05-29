package rnd.travelapp.cache;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import rnd.travelapp.utils.CollectionUtils;
import rnd.travelapp.utils.Failable;
import rnd.travelapp.utils.StreamUtils;

public class DiskCacheMeta {
    private static final List<ValidatorEntry> EMPTY_VALIDATOR_LIST = new ArrayList<>();

    private final Map<String, DiskCacheEntry> entries;
    private final Validator validator;
    private final File meta;
    private final File root;
    private String knownLastModified;

    public DiskCacheMeta(Validator validator, File meta, File root) {
        this.entries = new HashMap<>();
        this.validator = validator;
        this.meta = meta;
        this.root = root;
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

    public Failable<List<ValidatorEntry>> verify() {
        return validator.getLastModified().process(lastModified -> {
           if(lastModified.equals(knownLastModified)) {
               return Failable.success(EMPTY_VALIDATOR_LIST);
           } else {
               return validator.validate(entries.values());
           }
        });
    }

    public void addIfMissing(String key, File file) {
        synchronized (entries) {
            entries.putIfAbsent(key, DiskCacheEntry.fromFile(file, key));

            // TODO: Is this too expensive?
            saveMeta();
        }
    }

    public void addOrUpdate(String key, File file, String checksum) {
        synchronized (entries) {
            entries.put(key, DiskCacheEntry.fromFile(file, key, checksum));

            // TODO: Is this too expensive?
            saveMeta();
        }
    }

    public void remove(String key) {
        synchronized (entries) {
            entries.remove(key);

            // TODO: Is this too expensive?
            saveMeta();
        }
    }

    public Optional<DiskCacheEntry> getEntry(String key) {
        synchronized (entries) {
            return Optional.ofNullable(entries.get(key));
        }
    }

    public File getFile(String key) {
        File file = root;

        for(String segment : key.split("/")) {
            file = new File(file, segment);
        }

        return file;
    }

    public String getPath(File file) {
        StringBuilder sb = new StringBuilder();

        for(File currentFile = file; !currentFile.equals(root); currentFile = currentFile.getParentFile()) {
            sb.insert(0, file.getName() + "/");
        }

        // NOTE: Assumes file is a child of root (and not equal to root).
        return sb.substring(0, sb.length() - 1);
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
}
