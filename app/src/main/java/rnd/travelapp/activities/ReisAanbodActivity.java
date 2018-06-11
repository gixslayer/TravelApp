package rnd.travelapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import rnd.travelapp.R;
import rnd.travelapp.adapters.FilterModelAdapter;
import rnd.travelapp.adapters.ModelAdapter;
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

        setFilters(text.isEmpty() ? null : text.split(" "));
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
        Intent intent = getIntent();

        listView.setAdapter(filterAdapter);
        setFilters(intent.hasExtra("filters") ? intent.getStringArrayExtra("filters") : null);

        return filterAdapter;
    }

    private void setFilters(String[] tags) {
        Stream<String> stream = tags == null ? Stream.empty() : Arrays.stream(tags);

        // Create a tag filter for each tag.
        filterAdapter.setFilters(stream
                .map(tag -> new TagFilter<>(ReisModel::getTags, tag))
                .collect(Collectors.toList())
        );
        // Add a filter that always passes so that no tag filters shows all results.
        filterAdapter.addFilter(instance -> true);

        // Apply the filters and invalidate the list view to force a redraw.
        filterAdapter.applyFilters();
        listView.invalidateViews();
    }
}
