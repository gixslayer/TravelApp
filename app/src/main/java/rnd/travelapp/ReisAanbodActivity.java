package rnd.travelapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import rnd.travelapp.adapters.ReisModelAdapter;
import rnd.travelapp.models.ReisModel;
import rnd.travelapp.utils.Failable;

public class ReisAanbodActivity extends CacheActivity {
    private ListView listView;
    private ReisModelAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        // Set View
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reis_aanbod);

        // Set listeners
        listView = findViewById(R.id.list_reis_aanbod);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(this, ReisModelActivity.class);
            intent.putExtra("model", adapter.getModelName(i));

            startActivity(intent);
        });
    }

    @Override
    protected void onCacheInitialized() {
        appCache.getOrFetchList("models/reismodel/list.json", ReisModel.class).onCompletion((Failable<Map<String, ReisModel>> result) -> {
            result.consume(models -> {
                adapter = new ReisModelAdapter(models, this);
                listView.setAdapter(adapter);
                listView.invalidateViews();
            });
        });
    }
}
