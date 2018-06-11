package rnd.travelapp.cache;

import java.io.File;

import rnd.travelapp.utils.Failable;

/**
 * Specifies the interface for fetching entries not found in the bottom level cache.
 */
public interface Fetcher {
    /**
     * Fetch the entry with the given key.
     * @param key the key of the entry
     * @param destination the file to store the fetched content
     * @return the destination file upon success, or the cause of the failure otherwise
     */
    Failable<File> fetch(String key, File destination);
}
