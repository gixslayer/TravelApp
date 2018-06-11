package rnd.travelapp.threading;

import android.os.AsyncTask;

import java.util.function.Consumer;
import java.util.function.Supplier;

import rnd.travelapp.utils.Action;
import rnd.travelapp.utils.Failable;

/**
 * Represents an async task with a result that is executed on a background thread.
 * @param <T> the type of the result
 */
public class Task<T> {
    protected final Supplier<T> task;

    Task(Supplier<T> task) {
        this.task = task;
    }

    /**
     * Sets the callback to call on the UI thread once the async task completes.
     * @param onCompletion the callback on completion
     */
    public void onCompletion(Consumer<T> onCompletion) {
        new TaskWrapper<>(task, onCompletion).execute();
    }

    /**
     * Creates a new async task with a result, which is not yet executed.
     * @param task the task to perform in a background thread
     * @param <U> the type returned by the task
     * @return the created task
     */
    public static <U> Task<U> create(Supplier<U> task) {
        return new Task<>(task);
    }

    /**
     * Creates a new async task without a result, which is not yet executed.
     * @param task the task to perform in a background thread
     * @return the created task
     */
    public static VoidTask create(Action task) {
        return new VoidTask(task);
    }

    /**
     * Creates a new async task with a result that could fail, which is not yet executed.
     * @param task the task to perform in the background thread
     * @param <U> the type returned by the task upon successful completion
     * @return the created task
     */
    public static <U> FailableTask<U> createFailable(Supplier<Failable<U>> task) {
        return new FailableTask<>(task);
    }

    /**
     * Creates and executes a new async task.
     * @param task the task to perform in the background thread
     * @param onCompletion the callback to call on the UI thread once the async task completes
     */
    public static void run(Action task, Action onCompletion) {
        create(task).onCompletion(onCompletion);
    }

    /**
     * Creates and executes a new async task, with an empty onCompletion callback method.
     * @param action the task to perform in the background thread
     */
    public static void run(Action action) {
        run(action, () -> {});
    }

    /**
     * Creates and executes a new async task that returns a result.
     * @param task the task to perform in the background thread
     * @param onCompletion the callback to call on the UI thread once the async task completes
     * @param <T> the type of the task result
     */
    public static <T> void run(Supplier<T> task, Consumer<T> onCompletion) {
        create(task).onCompletion(onCompletion);
    }

    /**
     * Creates and executes a new async task that returns a result.
     * @param task the task to perform in the background thread
     * @param onCompletion the callback to call on the UI thread once the async task completes
     * @param <T> the type of the task result
     */
    public static <T> void run(Supplier<T> task, Action onCompletion) {
        run(task, r -> onCompletion.perform());
    }

    /**
     * Creates and executes a new async task that returns a result, with an empty onCompletion callback method.
     * @param task the task to perform in the background thread
     * @param <T> the type of the task result
     */
    public static <T> void run(Supplier<T> task) {
        run(task, () -> {});
    }

    /**
     * Creates and executes a new async task that returns a result which could fail.
     * @param task the task to perform in the background thread
     * @param onSuccess the callback on success
     * @param onFailure the callback on failure
     * @param <T> the type of the task result
     */
    public static <T> void runFailable(Supplier<Failable<T>> task, Consumer<T> onSuccess, Consumer<Throwable> onFailure) {
        createFailable(task).onSuccess(onSuccess).orOnFailure(onFailure);
    }

    /**
     * Creates and executes a new async task that returns a result which could fail.
     * @param task the task to perform in the background thread
     * @param onSuccess the callback on success
     * @param onFailure the callback on failure
     * @param <T> the type of the task result
     */
    public static <T> void runFailable(Supplier<Failable<T>> task, Consumer<T> onSuccess, Action onFailure) {
        runFailable(task, onSuccess, cause -> onFailure.perform());
    }

    /**
     * Creates and executes a new async task that returns a result which could fail.
     * @param task the task to perform in the background thread
     * @param onSuccess the callback on success
     * @param <T> the type of the task result
     */
    public static <T> void runFailable(Supplier<Failable<T>> task, Consumer<T> onSuccess) {
        runFailable(task, onSuccess, () -> {});
    }

    private static class TaskWrapper<T> extends AsyncTask<Void, Void, T> {
        private final Supplier<T> task;
        private final Consumer<T> onCompletion;

        TaskWrapper(Supplier<T> task, Consumer<T> onCompletion) {
            this.task = task;
            this.onCompletion = onCompletion;
        }

        @Override
        protected T doInBackground(Void... voids) {
            return task.get();
        }

        @Override
        protected void onPostExecute(T t) {
            onCompletion.accept(t);
        }
    }
}
