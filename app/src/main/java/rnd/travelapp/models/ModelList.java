package rnd.travelapp.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.function.Consumer;

import rnd.travelapp.serialization.JSONLoadable;
import rnd.travelapp.serialization.JSONLoader;

@JSONLoadable(ModelList.Loader.class)
public class ModelList {
    public static final ModelList EMPTY = new ModelList(new String[0]);

    private final String[] models;

    private ModelList(String[] models) {
        this.models = models;
    }

    public void forEach(Consumer<String> keyConsumer) {
        for(String key : models) {
            keyConsumer.accept(key);
        }
    }

    public String[] getModels() {
        return models;
    }

    public int size() {
        return models.length;
    }

    protected static class Loader implements JSONLoader<ModelList> {

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
