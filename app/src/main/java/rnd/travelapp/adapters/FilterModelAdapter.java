package rnd.travelapp.adapters;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class FilterModelAdapter<T> extends ModelAdapter<T> {
    private final List<T> filteredModels;
    private final List<Filter<T>> filters;

    public FilterModelAdapter(Map<String, T> models, Context context, int layout) {
        super(models, context, layout);

        this.filteredModels = new ArrayList<>();
        this.filters = new ArrayList<>();
    }

    public void clearFilters() {
        filters.clear();
    }

    public void addFilter(Filter<T> filter) {
        filters.add(filter);
    }

    public void addFilters(Collection<Filter<T>> filters) {
        this.filters.addAll(filters);
    }

    public void setFilters(Filter<T> filter) {
        filters.clear();
        filters.add(filter);
    }

    public void setFilters(Collection<Filter<T>> filters) {
        this.filters.clear();
        this.filters.addAll(filters);
    }

    public Filter<T> replaceFilter(Filter<T> oldFilter, Filter<T> newFilter) {
        filters.remove(oldFilter);
        filters.add(newFilter);

        return newFilter;
    }

    public void applyFilters() {
        filteredModels.clear();

        getModels().stream().filter(this::filter).collect(Collectors.toCollection(() -> filteredModels));
    }

    private boolean filter(T instance) {
        return filters.stream().allMatch(filter -> filter.filter(instance));
    }

    @Override
    public int getCount() {
        return filteredModels.size();
    }

    @Override
    public T getModel(int i) {
        return filteredModels.get(i);
    }

    public List<T> getFilteredModels() {
        return filteredModels;
    }

}
