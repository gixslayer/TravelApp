package rnd.travelapp;

import android.app.Activity;
import android.os.Bundle;

import rnd.travelapp.cache.AppCache;
import rnd.travelapp.models.TestModel;

public class MainActivity extends Activity {
    private AppCache appCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCache.getFor(this).onCompletion(cache -> {
            appCache = cache;

            appCache.verify().onCompletion(this::onCacheInitialized);
        });
    }

    private void onCacheInitialized() {
        appCache.getOrFetch("models/testmodel.json", TestModel.class).onCompletion(m -> {
            TestModel model = m.orOnFailure(null);
        });
    }
}
