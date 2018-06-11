package rnd.travelapp.threading;

import android.os.AsyncTask;

import java.util.function.Consumer;
import java.util.function.Supplier;

import rnd.travelapp.utils.Failable;

/**
 * Represents an async task that has the possibility of failing.
 * @param <T> the result type of the task
 */
public class FailableTask<T> extends Task<Failable<T>> {
    private Consumer<T> onSuccess;

    FailableTask(Supplier<Failable<T>> task) {
        super(task);

        this.onSuccess = null;
    }

    /**
     * Sets the callback to call on the UI thread if the async task successfully completes.
     * This method must be called before orOnFailure, but does not yet start execution of this task.
     * @param onSuccess the callback on success
     * @return this instance
     */
    public FailableTask<T> onSuccess(Consumer<T> onSuccess) {
        if(this.onSuccess != null) {
            throw new IllegalStateException("onSuccess already called");
        }

        this.onSuccess = onSuccess;

        return this;
    }

    /**
     * Sets the callback to call on the UI thread if the async task fails to complete. This method
     * cannot be called before onSuccess is called, and starts executions of this task.
     * @param onFailure the callback on failure
     */
    public void orOnFailure(Consumer<Throwable> onFailure) {
        if(onSuccess == null) {
            throw new IllegalStateException("onSuccess not called");
        }

        new TaskWrapper<>(task, onSuccess, onFailure).execute();
    }

    /**
     * Invokes orOnFailure with an empty failure callback.
     */
    public void orIgnoreOnFailure() {
        orOnFailure(cause -> {});
    }

    private static class TaskWrapper<T> extends AsyncTask<Void, Void, Failable<T>> {
        private final Supplier<Failable<T>> task;
        private final Consumer<T> onSuccess;
        private final Consumer<Throwable> onFailure;

        TaskWrapper(Supplier<Failable<T>> task, Consumer<T> onSuccess, Consumer<Throwable> onFailure) {
            this.task = task;
            this.onSuccess = onSuccess;
            this.onFailure = onFailure;
        }

        @Override
        protected Failable<T> doInBackground(Void... voids) {
            return task.get();
        }

        @Override
        protected void onPostExecute(Failable<T> t) {
            t.consume(onSuccess, onFailure);
        }
    }
}
