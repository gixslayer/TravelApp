package rnd.travelapp.cache;

import java.util.Collection;
import java.util.List;

import rnd.travelapp.utils.Failable;

/**
 * Specifies the interface for validating entries in the bottom level cache.
 */
public interface Validator {
    /**
     * Gets the current last modification date of the bottom level cache data source.
     * @return the last modification date time string
     */
    Failable<String> getLastModified();

    /**
     * Validates the given list of bottom level cache entries
     * @param entries the entries to validate
     * @return a list of mutations to apply upon success, or the cause of failure otherwise
     */
    Failable<List<ValidatorEntry>> validate(Collection<DiskCacheEntry> entries);
}
