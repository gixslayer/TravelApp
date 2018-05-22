package rnd.travelapp.cache;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import rnd.travelapp.utils.StreamUtils;

public class DiskCacheEntry {
    private static final MessageDigest DIGEST;

    static {
        try {
            DIGEST = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not supported");
        }
    }

    private final File file;
    private final String path;
    private final String checksum;
    private final Date acquired;

    public DiskCacheEntry(File file, String path, String checksum, Date acquired) {
        this.file = file;
        this.path = path;
        this.checksum = checksum;
        this.acquired = acquired;
    }

    public File getFile() {
        return file;
    }

    public String getPath() {
        return path;
    }

    public String getChecksum() {
        return checksum;
    }

    public Date getAcquired() {
        return acquired;
    }

    public JSONObject toMeta() throws JSONException {
        JSONObject object = new JSONObject();

        object.put("file", path);
        object.put("checksum", checksum);
        object.put("acquired", acquired.getTime());

        return object;
    }

    public static DiskCacheEntry fromFile(File file, String path) {
        String checksum = getChecksum(file);
        Date acquired = new Date(file.lastModified());

        return new DiskCacheEntry(file, path, checksum, acquired);
    }

    public static DiskCacheEntry fromMeta(JSONObject meta) throws JSONException {
        String path = meta.getString("file");
        String checksum = meta.getString("checksum");
        Date acquired = new Date(meta.getInt("acquired"));

        return new DiskCacheEntry(null, path, checksum, acquired);
    }

    public static DiskCacheEntry update(DiskCacheEntry entry, File file) {
        return new DiskCacheEntry(file, entry.path, entry.checksum, entry.acquired);
    }

    private static String getChecksum(File file) {
        try (FileInputStream stream = new FileInputStream(file)) {
            byte[] hash = StreamUtils.checksum(stream, DIGEST);

            return toHex(hash);
        } catch (IOException e) {
            Log.e("CACHE_APP", "Could not compute checksum", e);
        }

        return "";
    }

    private static String toHex(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);

        for(byte b : data) {
            sb.append(Character.forDigit((b >> 4) & 0xf, 16));
            sb.append(Character.forDigit(b & 0xf, 16));
        }

        return sb.toString();
    }
}
