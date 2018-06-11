package rnd.travelapp.adapters;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Adapter wrapper that has filtering capabilities.
 * @param <T> the model type
 */
public abstract class FilterModelAdapter<T> extends ModelAdapter<T> {
    private final List<T> filteredModels;
    private final List<Predicate<T>> filters;

    public FilterModelAdapter(Map<String, T> models, Context context, int layout) {
        super(models, context, layout);

        this.filteredModels = new ArrayList<>();
        this.filters = new ArrayList<>();
    }

    /**
     * Removes all filters.
     */
    public void clearFilters() {
        filters.clear();
    }

    /**
     * Adds a filter.
     * @param filter the filter to add
     */
    public void addFilter(Predicate<T> filter) {
        filters.add(filter);
    }

    /**
     * Adds a collection of filters.
     * @param filters the filters to add
     */
    public void addFilters(Collection<Predicate<T>> filters) {
        this.filters.addAll(filters);
    }

    /**
     * Removes all existing filters, and then adds the given filter.
     * @param filter the filter to add
     */
    public void setFilters(Predicate<T> filter) {
        filters.clear();
        filters.add(filter);
    }

    /**
     * Removes all existing filters, and then adds the given collection of filters.
     * @param filters the filters to add
     */
    public void setFilters(Collection<Predicate<T>> filters) {
        this.filters.clear();
        this.filters.addAll(filters);
    }

    /**
     * Applies the filters to update the list of visible entries.
     */
    public void applyFilters() {
        filteredModels.clear();

        getModels().stream().filter(this::filter).collect(Collectors.toCollection(() -> filteredModels));
    }

    private boolean filter(T instance) {
        return filters.stream().allMatch(filter -> filter.test(instance));
    }

    @Override
    public int getCount() {
        return filteredModels.size();
    }

    @Override
    public T getModel(int i) {
        return filteredModels.get(i);
    }

    /**
     * Returns the list of currently visible entries.
     * @return the visible entries
     */
    public List<T> getFilteredModels() {
        return filteredModels;
    }

}
