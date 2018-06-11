package rnd.travelapp.adapters;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Map;

import rnd.travelapp.R;
import rnd.travelapp.models.KuurModel;

public class KuurModelAdapter extends FilterModelAdapter<KuurModel> {
    public KuurModelAdapter(Map<String, KuurModel> models, Context context) {
        super(models, context, R.layout.list_item_kuur);
    }

    @Override
    protected void populateView(View view, KuurModel model) {
        TextView titleView = view.findViewById(R.id.list_item_kuur_title);
        TextView shortDescriptionView = view.findViewById(R.id.list_item_kuur_short_description);
        ImageView thumbnailView = view.findViewById(R.id.list_item_kuur_thumbnail);

        titleView.setText(model.getKuurTitel());
        shortDescriptionView.setText(model.getKuurKorteBeschrijving());
        model.getKuurAfbeelding().getOrFetchToImageView(context, thumbnailView);
    }
}
