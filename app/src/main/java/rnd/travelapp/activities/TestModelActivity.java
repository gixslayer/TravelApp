package rnd.travelapp.activities;

import android.os.Bundle;
import android.widget.ImageView;

import rnd.travelapp.R;
import rnd.travelapp.models.TestModel;

public class TestModelActivity extends ModelActivity<TestModel> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_model);
    }

    @Override
    protected void populateFromModel(TestModel model) {
        ImageView view = findViewById(R.id.test_model_image);

        model.getBitmap().getOrFetchToImageView(appCache, view);
    }

    @Override
    protected Class<TestModel> getModelType() {
        return TestModel.class;
    }
}
