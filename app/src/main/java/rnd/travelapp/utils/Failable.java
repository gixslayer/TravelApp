package rnd.travelapp.utils;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Failable<T> {
    private final boolean succeeded;
    private final T instance;
    private final Throwable cause;

    private Failable(boolean succeeded, T instance, Throwable cause) {
        this.succeeded = succeeded;
        this.instance = instance;
        this.cause = cause;
    }

    public boolean hasSucceeded() {
        return succeeded;
    }

    public boolean hasFailed() {
        return !succeeded;
    }

    public Throwable getCause() {
        if(!succeeded) {
            throw new IllegalStateException("The operation did not fail");
        }

        return cause;
    }

    public Optional<Throwable> getCauseIfFailed() {
        return Optional.ofNullable(cause);
    }

    public T get() {
        if(!succeeded) {
            throw new IllegalStateException("The operation did not succeed");
        }

        return instance;
    }

    public Optional<T> getIfSucceeded() {
        return Optional.ofNullable(instance);
    }

    public <U> Failable<U> process(Function<T, Failable<U>> onSuccess, Consumer<Throwable> onFailure) {
        if(succeeded) {
            return onSuccess.apply(instance);
        } else {
            onFailure.accept(cause);

            return Failable.failure(cause);
        }
    }

    public <U> Failable<U> process(Function<T, Failable<U>> onSuccess, Action onFailure) {
        return process(onSuccess, cause -> onFailure.perform());
    }

    public <U> Failable<U> process(Function<T, Failable<U>> onSuccess) {
        return process(onSuccess, cause -> {});
    }

    public <U> Failable<U> processSafe(Function<T, U> onSuccess, Consumer<Throwable> onFailure) {
        return process(instance -> Failable.success(onSuccess.apply(instance)));
    }

    public <U> Failable<U> processSafe(Function<T, U> onSuccess, Action onFailure) {
        return processSafe(onSuccess, cause -> onFailure.perform());
    }

    public <U> Failable<U> processSafe(Function<T, U> onSuccess) {
        return processSafe(onSuccess, cause -> {});
    }

    public T orOnFailure(T value) {
        return succeeded ? instance : value;
    }

    public T orOnFailureGet(Supplier<T> supplier) {
        return succeeded ? instance : supplier.get();
    }

    public static <T> Failable<T> success(T instance) {
        assert instance != null;

        return new Failable<>(true, instance, null);
    }

    public static <T> Failable<T> failure(Throwable cause) {
        assert cause != null;

        return new Failable<>(false, null, cause);
    }
}
