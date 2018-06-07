package rnd.travelapp.adapters;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;
import java.util.Map;

import rnd.travelapp.R;
import rnd.travelapp.models.TestModel;

public class TestModelAdapter extends ModelAdapter<TestModel> {
    public TestModelAdapter(Map<String, TestModel> models, Context context) {
        super(models, context, R.layout.list_item_main);
    }

    @Override
    protected void populateView(View view, TestModel model) {
        TextView title = view.findViewById(R.id.main_list_title);
        TextView index = view.findViewById(R.id.main_list_index);
        TextView value = view.findViewById(R.id.main_list_value);

        title.setText(model.getStringValue());
        index.setText(String.format(Locale.US, "index: %d", model.getIntValue()));
        value.setText(String.format(Locale.US, "value: %.2f", model.getDoubleValue()));
    }
}
