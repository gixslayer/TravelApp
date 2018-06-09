package rnd.travelapp.threading;

import android.os.AsyncTask;

import java.util.function.Consumer;
import java.util.function.Supplier;

import rnd.travelapp.utils.Action;
import rnd.travelapp.utils.Failable;

public class Task<T> {
    protected final Supplier<T> task;

    Task(Supplier<T> task) {
        this.task = task;
    }

    public void onCompletion(Consumer<T> onCompletion) {
        new TaskWrapper<>(task, onCompletion).execute();
    }

    public static <U> Task<U> create(Supplier<U> task) {
        return new Task<>(task);
    }

    public static VoidTask create(Action task) {
        return new VoidTask(task);
    }

    public static <U> FailableTask<U> createFailable(Supplier<Failable<U>> task) {
        return new FailableTask<>(task);
    }

    public static void run(Action task, Action onCompletion) {
        create(task).onCompletion(onCompletion);
    }

    public static void run(Action action) {
        run(action, () -> {});
    }

    public static <T> void run(Supplier<T> task, Consumer<T> onCompletion) {
        create(task).onCompletion(onCompletion);
    }

    public static <T> void run(Supplier<T> task, Action onCompletion) {
        run(task, r -> onCompletion.perform());
    }

    public static <T> void run(Supplier<T> task) {
        run(task, () -> {});
    }

    public static <T> void runFailable(Supplier<Failable<T>> task, Consumer<T> onSuccess, Consumer<Throwable> onFailure) {
        createFailable(task).onSuccess(onSuccess).orOnFailure(onFailure);
    }

    public static <T> void runFailable(Supplier<Failable<T>> task, Consumer<T> onSuccess, Action onFailure) {
        runFailable(task, onSuccess, cause -> onFailure.perform());
    }

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
