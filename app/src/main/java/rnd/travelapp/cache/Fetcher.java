package rnd.travelapp.cache;

import java.io.File;

import rnd.travelapp.utils.Failable;

public interface Fetcher {
    Failable<File> fetch(String key, File destination);
}
