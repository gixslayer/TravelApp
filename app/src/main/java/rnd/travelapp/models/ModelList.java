package rnd.travelapp.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.function.Consumer;

import rnd.travelapp.serialization.JSONLoadable;
import rnd.travelapp.serialization.JSONLoader;

/**
 * Wraps a list of models of the same type, which is used by the AppCache to fetch a list of models.
 */
@JSONLoadable(ModelList.Loader.class)
public class ModelList {
    /**
     * An empty list of models.
     */
    public static final ModelList EMPTY = new ModelList(new String[0]);

    private final String[] models;

    private ModelList(String[] models) {
        this.models = models;
    }

    /**
     * Applies an operation to each model path in this list.
     * @param keyConsumer the operation to perform
     */
    public void forEach(Consumer<String> keyConsumer) {
        for(String key : models) {
            keyConsumer.accept(key);
        }
    }

    /**
     * Returns the array of model paths in this list.
     * @return the array of model paths
     */
    public String[] getModels() {
        return models;
    }

    /**
     * Returns the number of models in this list.
     * @return the number of models
     */
    public int size() {
        return models.length;
    }

    public static class Loader implements JSONLoader<ModelList> {

        @Override
        public ModelList deserialize(JSONObject object) throws JSONException {
            JSONArray array = object.getJSONArray("models");
            String[] models = new String[array.length()];

            for(int i = 0; i < models.length; ++i) {
                models[i] = array.getString(i);
            }

            return new ModelList(models);
        }

        @Override
        public Class<ModelList> getType() {
            return ModelList.class;
        }
    }
}
