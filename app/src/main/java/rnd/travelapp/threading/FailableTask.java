package rnd.travelapp.threading;

import android.os.AsyncTask;

import java.util.function.Consumer;
import java.util.function.Supplier;

import rnd.travelapp.utils.Failable;

public class FailableTask<T> extends Task<Failable<T>> {
    private Consumer<T> onSuccess;

    FailableTask(Supplier<Failable<T>> task) {
        super(task);

        this.onSuccess = null;
    }

    public FailableTask<T> onSuccess(Consumer<T> onSuccess) {
        if(this.onSuccess != null) {
            throw new IllegalStateException("onSuccess already called");
        }

        this.onSuccess = onSuccess;

        return this;
    }

    public void orOnFailure(Consumer<Throwable> onFailure) {
        if(onSuccess == null) {
            throw new IllegalStateException("onSuccess not called");
        }

        new TaskWrapper<>(task, onSuccess, onFailure).execute();
    }

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
