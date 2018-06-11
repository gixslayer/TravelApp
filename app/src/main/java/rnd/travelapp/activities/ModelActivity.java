package rnd.travelapp.activities;

import android.util.Log;

/**
 * Base for activities that display a model.
 * @param <T> the model type
 */
public abstract class ModelActivity<T> extends CacheActivity {
    public static final String MODEL_KEY = "model";

    @Override
    protected void onCacheInitialized() {
        String key = getIntent().getStringExtra(MODEL_KEY);

        appCache.getOrFetch(key, getModelType())
                .onSuccess(this::populateFromModel)
                .orOnFailure(this::handleMissingModel);
    }

    /**
     * Populates this activity from the given model instance.
     * @param model the model instance
     */
    protected void populateFromModel(T model) { }

    private void handleMissingModel(Throwable cause) {
        String key = getIntent().getStringExtra("model");

        Log.e("TRAVEL_APP", "Missing model: " + key, cause);

        finish();
    }

    /**
     * Returns the type of the model, which is lost at runtime due to type erasure.
     * @return the model type
     */
    protected abstract Class<T> getModelType();
}
