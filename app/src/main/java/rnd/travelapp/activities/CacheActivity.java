package rnd.travelapp.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import rnd.travelapp.cache.AppCache;

/**
 * Base for activities that use the AppCache.
 */
public abstract class CacheActivity extends Activity {
    protected AppCache appCache;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onBeginCacheInitialization();

        AppCache.getFor(getApplicationContext()).onCompletion(cache -> {
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

    /**
     * Invoked right before the AppCache begins initialization.
     */
    protected  void onBeginCacheInitialization() {

    }

    /**
     * Invoked once the AppCache has been initialized.
     */
    protected void onCacheInitialized() {

    }
}
