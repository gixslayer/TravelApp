package rnd.travelapp.threading;

import android.os.AsyncTask;

import java.util.function.Consumer;

import rnd.travelapp.utils.Action;

public class VoidTask extends Task<Void> {
    private final Action task;

    VoidTask(Action task) {
        super(() -> {
            task.perform();

            return null;
        });

        this.task = task;
    }

    @Override
    public void onCompletion(Consumer<Void> onCompletion) {
        onCompletion(() -> onCompletion.accept(null));
    }

    public void onCompletion(Action onCompletion) {
        new TaskWrapper(task, onCompletion).execute();
    }

    private static class TaskWrapper extends AsyncTask<Void, Void, Void> {
        private final Action task;
        private final Action onCompletion;

        TaskWrapper(Action task, Action onCompletion) {
            this.task = task;
            this.onCompletion = onCompletion;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            task.perform();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            onCompletion.perform();
        }
    }
}
