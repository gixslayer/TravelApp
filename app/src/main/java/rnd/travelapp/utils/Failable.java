package rnd.travelapp.utils;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a wrapper class on a result which might not exist due to a failure. This wrapper is
 * designed to wrap exceptions in a manner that aids a functional/declarative programming style.
 * @param <T>
 */
public class Failable<T> {
    private final boolean succeeded;
    private final T instance;
    private final Throwable cause;

    private Failable(boolean succeeded, T instance, Throwable cause) {
        this.succeeded = succeeded;
        this.instance = instance;
        this.cause = cause;
    }

    /**
     * Checks if the result succeeded.
     * @return true if the result succeeded, false otherwise
     */
    public boolean hasSucceeded() {
        return succeeded;
    }

    /**
     * Check if the result failed.
     * @return true if the result failed, false otherwise
     */
    public boolean hasFailed() {
        return !succeeded;
    }

    /**
     * Returns the cause of the failure. If the result did not fail, this will throw an exception.
     * @return the cause of the failure
     */
    public Throwable getCause() {
        if(succeeded) {
            throw new IllegalStateException("The operation did not fail");
        }

        return cause;
    }

    /**
     * Returns the cause of the failure, if the result failed.
     * @return the cause of the failure, or an empty Optional if the result did not fail
     */
    public Optional<Throwable> getCauseIfFailed() {
        return Optional.ofNullable(cause);
    }

    public T get() {
        if(!succeeded) {
            throw new IllegalStateException("The operation did not succeed");
        }

        return instance;
    }

    /**
     * Returns the result, if the result succeeded.
     * @return the result, or an empty Optional if the result did not succeed
     */
    public Optional<T> getIfSucceeded() {
        return Optional.ofNullable(instance);
    }

    /**
     * Perform an operation on the result if the result succeeded.
     * @param consumer the operation to perform
     */
    public void ifSucceeded(Consumer<T> consumer) {
        if(succeeded) {
            consumer.accept(instance);
        }
    }

    /**
     * Maps this result to a new Failable result using onSuccess if this result has succeeded, or
     * returning failure with the cause of this result if this result did not succeed.
     * @param onSuccess the map function to invoke if this result succeeded
     * @param onFailure the function to invoke if this result failed
     * @param <U> the result type of the map function
     * @return the mapped Failable
     */
    public <U> Failable<U> process(Function<T, Failable<U>> onSuccess, Consumer<Throwable> onFailure) {
        if(succeeded) {
            return onSuccess.apply(instance);
        } else {
            onFailure.accept(cause);

            return Failable.failure(cause);
        }
    }

    /**
     * Consume this result using onSuccess if this result has succeeded, or
     * onFailure with the cause of this result if this result did not succeed.
     * @param onSuccess the map function to invoke if this result succeeded
     * @param onFailure the function to invoke if this result failed
     */
    public void consume(Consumer<T> onSuccess, Consumer<Throwable> onFailure) {
        if(succeeded) {
            onSuccess.accept(instance);
        } else {
            onFailure.accept(cause);
        }
    }

    /**
     * Consume this result using onSuccess if this result has succeeded, or
     * onFailure with the cause of this result if this result did not succeed.
     * @param onSuccess the map function to invoke if this result succeeded
     * @param onFailure the function to invoke if this result failed
     */
    public void consume(Consumer<T> onSuccess, Action onFailure) {
        consume(onSuccess, cause -> onFailure.perform());
    }

    /**
     * Consume this result using onSuccess if this result has succeeded.
     * @param onSuccess the map function to invoke if this result succeeded
     */
    public void consume(Consumer<T> onSuccess) {
        consume(onSuccess, cause -> {});
    }

    /**
     * Maps this result to a new Failable result using onSuccess if this result has succeeded, or
     * returning failure with the cause of this result if this result did not succeed.
     * @param onSuccess the map function to invoke if this result succeeded
     * @param onFailure the function to invoke if this result failed
     * @param <U> the result type of the map function
     * @return the mapped Failable
     */
    public <U> Failable<U> process(Function<T, Failable<U>> onSuccess, Action onFailure) {
        return process(onSuccess, cause -> onFailure.perform());
    }

    /**
     * Maps this result to a new Failable result using onSuccess if this result has succeeded, or
     * returning failure with the cause of this result if this result did not succeed.
     * @param onSuccess the map function to invoke if this result succeeded
     * @param <U> the result type of the map function
     * @return the mapped Failable
     */
    public <U> Failable<U> process(Function<T, Failable<U>> onSuccess) {
        return process(onSuccess, cause -> {});
    }

    /**
     * Maps this result to a new Failable result using onSuccess if this result has succeeded, or
     * returning failure with the cause of this result if this result did not succeed.
     * @param onSuccess the map function to invoke if this result succeeded
     * @param onFailure the function to invoke if this result failed
     * @param <U> the result type of the map function
     * @return the mapped Failable
     */
    public <U> Failable<U> processSafe(Function<T, U> onSuccess, Consumer<Throwable> onFailure) {
        return process(instance -> Failable.success(onSuccess.apply(instance)));
    }

    /**
     * Maps this result to a new Failable result using onSuccess if this result has succeeded, or
     * returning failure with the cause of this result if this result did not succeed.
     * @param onSuccess the map function to invoke if this result succeeded
     * @param onFailure the function to invoke if this result failed
     * @param <U> the result type of the map function
     * @return the mapped Failable
     */
    public <U> Failable<U> processSafe(Function<T, U> onSuccess, Action onFailure) {
        return processSafe(onSuccess, cause -> onFailure.perform());
    }

    /**
     * Maps this result to a new Failable result using onSuccess if this result has succeeded.
     * @param onSuccess the map function to invoke if this result succeeded
     * @param <U> the result type of the map function
     * @return the mapped Failable
     */
    public <U> Failable<U> processSafe(Function<T, U> onSuccess) {
        return processSafe(onSuccess, cause -> {});
    }

    /**
     * Return the result if this result succeeded, or the given value if this result failed.
     * @param value the result upon failure
     * @return the result of this result, or the given value
     */
    public T orOnFailure(T value) {
        return succeeded ? instance : value;
    }

    /**
     * Return the result if this result succeeded, or get the result from the given supplier if this
     * result failed.
     * @param supplier the supplier to invoke if this result failed
     * @return the result of this result, or the result from the supplier
     */
    public T orOnFailureGet(Supplier<T> supplier) {
        return succeeded ? instance : supplier.get();
    }

    /**
     * Create a new succeeded Failable from the given instance
     * @param instance the result instance
     * @param <T> the result type
     * @return the created Failable
     */
    public static <T> Failable<T> success(T instance) {
        assert instance != null;

        return new Failable<>(true, instance, null);
    }

    /**
     * Create a new failed Failable from the given cause
     * @param cause the cause of the failure
     * @param <T> the result type
     * @return the created Failable
     */
    public static <T> Failable<T> failure(Throwable cause) {
        assert cause != null;

        return new Failable<>(false, null, cause);
    }
}
