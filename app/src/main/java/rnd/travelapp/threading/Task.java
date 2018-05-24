package rnd.travelapp.threading;

import android.os.AsyncTask;

import java.util.function.Consumer;
import java.util.function.Supplier;

import rnd.travelapp.utils.Action;

public class Task<T> {
    private final Supplier<T> task;

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
