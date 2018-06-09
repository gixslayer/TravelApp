package rnd.travelapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Map;

import rnd.travelapp.Booking;
import rnd.travelapp.R;
import rnd.travelapp.adapters.ModelAdapter;
import rnd.travelapp.adapters.ReisModelAdapter;
import rnd.travelapp.models.ReisModel;

public class ReisAanbodActivity extends ModelAdapterActivity<ReisModel> {
    private ListView listView;

    protected void onCreate(Bundle savedInstanceState) {
        // Set View
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reis_aanbod);

        // Set listeners
        listView = findViewById(R.id.list_reis_aanbod);
        listView.setOnItemClickListener((adapterView, view, i, l) -> openModel(i, ReisModelActivity.class));

        new Booking(this, "test").send()
                .onSuccess(bookingID -> Toast.makeText(this, Integer.toString(bookingID), Toast.LENGTH_SHORT).show())
                .orOnFailure(cause -> Log.e("TRAVEL_APP", "Booking failed", cause));
    }

    @Override
    protected String getModelListPath() {
        return "models/reismodel/list.json";
    }

    @Override
    protected Class<ReisModel> getModelType() {
        return ReisModel.class;
    }

    @Override
    protected ModelAdapter<ReisModel> createAdapter(Map<String, ReisModel> models) {
        ModelAdapter<ReisModel> adapter = new ReisModelAdapter(models, this);

        listView.setAdapter(adapter);
        listView.invalidateViews();

        return adapter;
    }
}
