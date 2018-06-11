package rnd.travelapp.adapters;

import android.content.Context;
import android.view.View;

import java.util.Map;

import rnd.travelapp.R;
import rnd.travelapp.models.KuurModel;

public class KuurModelAdapter extends ModelAdapter<KuurModel> {
    public KuurModelAdapter(Map<String, KuurModel> models, Context context) {
        super(models, context, R.layout.list_item_reis);
    }

    @Override
    protected void populateView(View view, KuurModel model) {

    }
}
