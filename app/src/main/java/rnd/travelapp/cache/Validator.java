package rnd.travelapp.cache;

import java.util.List;

import rnd.travelapp.utils.Failable;

public interface Validator {
    Failable<String> getLastModified();
    Failable<List<ValidatorEntry>> validate(List<DiskCacheEntry> entries);
}
