package rnd.travelapp.adapters;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Map;

import rnd.travelapp.R;
import rnd.travelapp.models.ReisModel;

/**
 * Adapter for the overview of destinations.
 */
public class ReisModelAdapter extends FilterModelAdapter<ReisModel>{
    public ReisModelAdapter(Map<String, ReisModel> models, Context context) {
        super(models, context, R.layout.list_item_reis);
    }

    @Override
    protected void populateView(View view, ReisModel model) {
        TextView titleView = view.findViewById(R.id.list_item_reis_title);
        TextView shortDescriptionView = view.findViewById(R.id.list_item_reis_short_description);
        ImageView thumbnailView = view.findViewById(R.id.list_item_reis_thumbnail);

        titleView.setText(model.getReisTitel());
        shortDescriptionView.setText(model.getReisKorteBeschrijving());
        model.getReisThumbnail().getOrFetchToImageView(context, thumbnailView);
    }
}
