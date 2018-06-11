package rnd.travelapp.adapters;

public class PassFilter<T> implements Filter<T> {
    @Override
    public boolean filter(T instance) {
        return true;
    }
}
