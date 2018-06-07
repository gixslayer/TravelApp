package rnd.travelapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import rnd.travelapp.R;
import rnd.travelapp.models.TestModel;

public class TestModelActivity extends CacheActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_model);
    }

    @Override
    protected void onCacheInitialized() {
        String key = getIntent().getStringExtra("model");

        appCache.getOrFetch(key, TestModel.class).onCompletion(result ->
                result.consume(this::populateFromModel, this::handleMissingModel));
    }

    private void populateFromModel(TestModel model) {
        model.getBitmap().getOrFetch(this, result -> result.consume(bitmap -> {
            ImageView view = findViewById(R.id.test_model_image);

            view.setImageBitmap(bitmap);
        }, cause -> {
            Log.e("TRAVEL_APP", "Could not fetch image", cause);
        }));
    }

    private void handleMissingModel(Throwable cause) {
        String key = getIntent().getStringExtra("model");

        Log.e("TRAVEL_APP", "Missing model: " + key, cause);

        finish();
    }
}
