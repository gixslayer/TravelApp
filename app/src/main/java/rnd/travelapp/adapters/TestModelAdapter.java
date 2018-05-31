package rnd.travelapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import rnd.travelapp.R;
import rnd.travelapp.models.TestModel;

public class TestModelAdapter extends BaseAdapter {
    private final List<TestModel> models;
    private final Map<TestModel, String> modelNames;
    private final Context context;

    public TestModelAdapter(Map<String, TestModel> models, Context context) {
        this.models = new ArrayList<>(models.values());
        this.modelNames = new HashMap<>();
        this.context = context;

        for(String name : models.keySet()) {
            modelNames.put(models.get(name), name);
        }
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public Object getItem(int position) {
        return models.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String getModelName(int position) {
        return modelNames.get(models.get(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if(convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_main, parent, false);
        }

        TextView title = view.findViewById(R.id.main_list_title);
        TextView index = view.findViewById(R.id.main_list_index);
        TextView value = view.findViewById(R.id.main_list_value);
        TestModel model = models.get(position);

        title.setText(model.getStringValue());
        index.setText(String.format(Locale.US, "index: %d", model.getIntValue()));
        value.setText(String.format(Locale.US, "value: %.2f", model.getDoubleValue()));

        return view;
    }
}
