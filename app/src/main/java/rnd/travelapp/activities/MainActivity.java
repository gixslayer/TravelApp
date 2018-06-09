package rnd.travelapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import rnd.travelapp.R;
import rnd.travelapp.adapters.TestModelAdapter;
import rnd.travelapp.models.TestModel;

public class MainActivity extends VerifyCacheActivity {
    private ListView listView;
    private TestModelAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.list_main);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(this, TestModelActivity.class);
            intent.putExtra("model", adapter.getModelPath(i));

            startActivity(intent);
        });
    }

    @Override
    protected void onCacheVerified() {
        appCache.getOrFetchList("models/testmodel/list.json", TestModel.class)
                .onSuccess(models -> {
                    adapter = new TestModelAdapter(models, this);
                    listView.setAdapter(adapter);
                    listView.invalidateViews();
                }).orOnFailure(cause -> Log.e("TRAVEL_APP", "Failed to fetch list", cause));
    }
}
