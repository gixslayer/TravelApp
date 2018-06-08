package rnd.travelapp.adapters;

public interface Filter<T> {
    boolean filter(T instance);
}
