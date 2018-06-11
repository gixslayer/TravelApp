package rnd.travelapp.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Map;

import rnd.travelapp.Booking;
import rnd.travelapp.R;
import rnd.travelapp.adapters.FilterModelAdapter;
import rnd.travelapp.adapters.ModelAdapter;
import rnd.travelapp.adapters.PassFilter;
import rnd.travelapp.adapters.KuurModelAdapter;
import rnd.travelapp.adapters.TagFilter;
import rnd.travelapp.models.KuurModel;

public class KuurAanbodActivity extends ModelAdapterActivity<KuurModel> {
    private ListView listView;
    private FilterModelAdapter<KuurModel> filterAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        // Set View
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kuur_aanbod);

        // Set listeners
        listView = findViewById(R.id.list_kuur_aanbod);
        listView.setOnItemClickListener((adapterView, view, i, l) -> openModel(i, KuurModelActivity.class));

        new Booking(this, "test").send()
                .onSuccess(bookingID -> Toast.makeText(this, Integer.toString(bookingID), Toast.LENGTH_SHORT).show())
                .orOnFailure(cause -> Log.e("TRAVEL_APP", "Booking failed", cause));

        EditText filterText = findViewById(R.id.kuur_filter);

        filterText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                onFilterChanged(editable.toString());
            }
        });
    }

    private void onFilterChanged(String text) {
        // When the screen is rotated the Activity is recreated, and this method might be invoked
        // before filterAdapter has been set again, causing a crash.
        if(filterAdapter == null) {
            return;
        }

        filterAdapter.clearFilters();

        if(text.isEmpty()) {
            filterAdapter.addFilter(new PassFilter<>());
        } else {
            Arrays.stream(text.split(" ")).forEach(tag -> {
                filterAdapter.addFilter(new TagFilter<>(KuurModel::getTags, tag));
            });
        }

        filterAdapter.applyFilters();
        listView.invalidateViews();
    }

    @Override
    protected String getModelListPath() {
        return "models/kuurmodel/list.json";
    }

    @Override
    protected Class<KuurModel> getModelType() {
        return KuurModel.class;
    }

    @Override
    protected ModelAdapter<KuurModel> createAdapter(Map<String, KuurModel> models) {
        filterAdapter = new KuurModelAdapter(models, this);
        filterAdapter.setFilters(new PassFilter<>());
        filterAdapter.applyFilters();

        listView.setAdapter(filterAdapter);
        listView.invalidateViews();

        return filterAdapter;
    }
}
