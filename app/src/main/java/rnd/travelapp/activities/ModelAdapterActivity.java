package rnd.travelapp.activities;

import android.content.Intent;
import android.util.Log;

import java.util.Map;

import rnd.travelapp.adapters.ModelAdapter;

public abstract class ModelAdapterActivity<T> extends CacheActivity {
    protected ModelAdapter<T> adapter;

    @Override
    protected void onCacheInitialized() {
        appCache.getOrFetchList(getModelListPath(), getModelType()).onCompletion(result -> {
            result.consume(
                    models -> adapter = createAdapter(models),
                    cause -> Log.e("TRAVEL_APP", "Failed to fetch list", cause)
            );
        });
    }

    protected void openModel(int position, Class<?> activity) {
        Intent intent = new Intent(this, activity);
        intent.putExtra(ModelActivity.MODEL_KEY, adapter.getModelPath(position));

        startActivity(intent);
    }

    protected abstract String getModelListPath();
    protected abstract Class<T> getModelType();
    protected abstract ModelAdapter<T> createAdapter(Map<String, T> models);
}
