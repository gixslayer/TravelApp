package rnd.travelapp.activities;

import android.util.Log;

public abstract class ModelActivity<T> extends CacheActivity {
    public static final String MODEL_KEY = "model";

    @Override
    protected void onCacheInitialized() {
        String key = getIntent().getStringExtra(MODEL_KEY);

        appCache.getOrFetch(key, getModelType())
                .onSuccess(this::populateFromModel)
                .orOnFailure(this::handleMissingModel);
    }

    protected void populateFromModel(T model) { }

    private void handleMissingModel(Throwable cause) {
        String key = getIntent().getStringExtra("model");

        Log.e("TRAVEL_APP", "Missing model: " + key, cause);

        finish();
    }

    protected abstract Class<T> getModelType();
}
