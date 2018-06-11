package rnd.travelapp.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

import java.util.Arrays;
import java.util.Map;

import rnd.travelapp.R;
import rnd.travelapp.adapters.FilterModelAdapter;
import rnd.travelapp.adapters.ModelAdapter;
import rnd.travelapp.adapters.PassFilter;
import rnd.travelapp.adapters.ReisModelAdapter;
import rnd.travelapp.adapters.TagFilter;
import rnd.travelapp.models.ReisModel;

public class ReisAanbodActivity extends ModelAdapterActivity<ReisModel> {
    private ListView listView;
    private FilterModelAdapter<ReisModel> filterAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        // Set View
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reis_aanbod);

        // Set listeners
        listView = findViewById(R.id.list_reis_aanbod);
        listView.setOnItemClickListener((adapterView, view, i, l) -> openModel(i, ReisModelActivity.class));

        EditText filterText = findViewById(R.id.reis_filter);

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
                filterAdapter.addFilter(new TagFilter<>(ReisModel::getTags, tag));
            });
        }

        filterAdapter.applyFilters();
        listView.invalidateViews();
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
        filterAdapter = new ReisModelAdapter(models, this);
        filterAdapter.setFilters(new PassFilter<>());
        filterAdapter.applyFilters();

        listView.setAdapter(filterAdapter);
        listView.invalidateViews();

        return filterAdapter;
    }
}
