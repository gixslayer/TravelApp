package rnd.travelapp.activities;

import android.content.Intent;
import android.util.Log;

import java.util.Map;

import rnd.travelapp.adapters.ModelAdapter;

/**
 * Base for activities that use a ModelAdapter to dynamically display models.
 * @param <T> the model type
 */
public abstract class ModelAdapterActivity<T> extends CacheActivity {
    protected ModelAdapter<T> adapter;

    @Override
    protected void onCacheInitialized() {
        appCache.getOrFetchList(getModelListPath(), getModelType())
                .onSuccess(models -> adapter = createAdapter(models))
                .orOnFailure(cause -> Log.e("TRAVEL_APP", "Failed to fetch list", cause));
    }

    /**
     * Opens the model in a new activity.
     * @param position the model adapter position
     * @param activity the activity to open
     */
    protected void openModel(int position, Class<?> activity) {
        Intent intent = new Intent(this, activity);
        intent.putExtra(ModelActivity.MODEL_KEY, adapter.getModelPath(position));

        startActivity(intent);
    }

    /**
     * Returns the path to the list of models to populate the adapter with.
     * @return the path to the model list
     */
    protected abstract String getModelListPath();

    /**
     * Returns the type of the model, which is lost at runtime due to type erasure.
     * @return the model type
     */
    protected abstract Class<T> getModelType();

    /**
     * Creates a ModelAdapter for the given list of model, model path pairs.
     * @param models the model, model path pairs
     * @return the created adapter
     */
    protected abstract ModelAdapter<T> createAdapter(Map<String, T> models);
}
