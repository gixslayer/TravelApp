package rnd.travelapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import rnd.travelapp.models.ReisModel;

public class ReisAdapter extends BaseAdapter {
    private Context context;
    private List<ReisModel> reizen;

    /**
     * Creates a new adapter without any puzzles.
     * @param context the context of this adapter
     */
    public ReisAdapter(Context context) {
        this(context, new ArrayList<>());
    }

    /**
     * Creates a new adapter with the given list of puzzles.
     * @param context the context of this adapter
     * @param reizen the list of puzzles
     */
    public ReisAdapter(Context context, List<ReisModel> reizen) {
        this.context = context;
        this.reizen = reizen;
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

    /**
     * Returns the list of reizen in this adapter.
     * @return the list of reizen
     */
    public List<ReisModel> getReizen() {
        return reizen;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ReisModel reis = reizen.get(i);
        View listItemView = view;

        if(listItemView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            listItemView = inflater.inflate(R.layout.list_item_reis, viewGroup, false);
        }

        TextView titleView = listItemView.findViewById(R.id.title);
        TextView descriptionView = listItemView.findViewById(R.id.short_description);
        ImageView thumbnailView = listItemView.findViewById(R.id.thumbnail);

        titleView.setText(reis.getAlgemeen().getTitel());
        descriptionView.setText(reis.getShortDescription());
        thumbnailView.setImageResource(R.drawable.image_1);

        return listItemView;
    }
}
