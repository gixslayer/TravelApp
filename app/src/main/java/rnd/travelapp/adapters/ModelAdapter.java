package rnd.travelapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple Adapter wrapper over a map of model, model path pairs.
 * @param <T> the model type
 */
public abstract class ModelAdapter<T> extends BaseAdapter {
    private final List<T> models;
    private final Map<T, String> modelPaths;
    protected final Context context;
    private final int layout;

    public ModelAdapter(Map<String, T> models, Context context, int layout) {
        this.models = new ArrayList<>(models.values());
        this.modelPaths = new HashMap<>();
        this.context = context;
        this.layout = layout;

        models.keySet().forEach(path -> modelPaths.put(models.get(path), path));
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public Object getItem(int i) {
        return getModel(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public T getModel(int i) {
        return models.get(i);
    }

    public String getModelPath(int i) {
        return modelPaths.get(getModel(i));
    }

    public List<T> getModels() {
        return models;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        T model = getModel(i);
        View view = convertView;

        if(convertView == null) {
            view = LayoutInflater.from(context).inflate(layout, parent, false);
        }

        populateView(view, model);

        return view;
    }

    /**
     * Populate the given view from the given model.
     * @param view the view to populate
     * @param model the model to populate the view with
     */
    protected abstract void populateView(View view, T model);
}
