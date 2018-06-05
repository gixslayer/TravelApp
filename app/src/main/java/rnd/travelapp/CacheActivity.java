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

        AppCache.getFor(this).onCompletion(cache -> {
            appCache = cache;

            onCacheInitialized();
        });
    }

    protected void onCacheInitialized() {

    }
}
