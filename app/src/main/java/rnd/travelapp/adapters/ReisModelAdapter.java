package rnd.travelapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rnd.travelapp.R;
import rnd.travelapp.models.ReisModel;

public class ReisModelAdapter extends BaseAdapter {
    private final List<ReisModel> reizen;
    private final Map<ReisModel, String> reisNames;
    private final Context context;

    public ReisModelAdapter(Map<String, ReisModel> models, Context context) {
        this.reizen = new ArrayList<>(models.values());
        this.reisNames = new HashMap<>();
        this.context = context;

        for(String name : models.keySet()) {
            reisNames.put(models.get(name), name);
        }
    }
    
    @Override
    public int getCount() {
        return reizen.size();
    }

    @Override
    public Object getItem(int i) {
        return getReis(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * Get the reis at the given position.
     * @param i the position
     * @return the puzzle at that position
     */
    public ReisModel getReis(int i) {
        return reizen.get(i);
    }

    public String getModelName(int i) {
        return reisNames.get(reizen.get(i));
    }

    /**
     * Returns the list of reizen in this adapter.
     * @return the list of reizen
     */
    public List<ReisModel> getReizen() {
        return reizen;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        ReisModel reis = reizen.get(i);
        View view = convertView;

        if(convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_reis, parent, false);
        }

        TextView titleView = view.findViewById(R.id.title);
        TextView shortDescriptionView = view.findViewById(R.id.short_description);
        ImageView thumbnailView = view.findViewById(R.id.thumbnail);

        titleView.setText(reis.getReisTitel());
        shortDescriptionView.setText(reis.getReisKorteBeschrijving());

        reis.getReisAfbeelding().getOrFetch(context, result -> result.consume(thumbnailView::setImageBitmap, cause -> {
            Log.e("TRAVEL_APP", "Could not fetch image", cause);
        }));

        return view;
    }
}
