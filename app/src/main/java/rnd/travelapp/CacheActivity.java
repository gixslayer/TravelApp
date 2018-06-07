package rnd.travelapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import rnd.travelapp.cache.AppCache;

public abstract class CacheActivity extends Activity {
    protected AppCache appCache;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onBeginCacheInitialization();

        AppCache.getFor(this).onCompletion(cache -> {
            appCache = cache;

            onCacheInitialized();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Make sure any changes to the cache meta are written to the meta file.
        appCache.saveChanges();
    }

    protected  void onBeginCacheInitialization() {

    }

    protected void onCacheInitialized() {

    }
}
