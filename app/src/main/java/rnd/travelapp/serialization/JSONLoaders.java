package rnd.travelapp.serialization;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class JSONLoaders {
    private static final Map<Class, JSONLoader> LOADERS = new HashMap<>();

    public static <T> T deserialize(String json, Class<T> type) throws JSONException {
        JSONLoader<T> loader = get(type);
        JSONObject object = new JSONObject(json);

        return loader.deserialize(object);
    }

    public static <T> JSONLoader<T> get(T instance) {
        return get(instance.getClass());
    }

    public static <T> JSONLoader<T> get(Class<?> type) {
        if(!LOADERS.containsKey(type)) {
            LOADERS.put(type, create(type));
        }

        //noinspection unchecked
        return LOADERS.get(type);
    }

    private static <T> JSONLoader<T> create(Class<T> type) {
        if(!type.isAnnotationPresent(JSONLoadable.class)) {
            throw new IllegalArgumentException(String.format(
                    "type %s is missing JSONLoadable attribute", type));
        }

        JSONLoadable loaderInfo = type.getAnnotation(JSONLoadable.class);
        Class<? extends JSONLoader> loaderType = loaderInfo.value();

        if(loaderType.isMemberClass() && !Modifier.isStatic(loaderType.getModifiers())) {
            throw new IllegalArgumentException(String.format(
                    "Loader type %s must be static because it is a member class", loaderType));
        }

        try {
            JSONLoader loader = loaderType.newInstance();
            Class<?> loadType = loader.getType();

            if(!loader.getType().equals(type)) {
                throw new IllegalArgumentException(String.format(
                        "type %s does not match JSONLoader type %s", type, loadType));
            }

            //noinspection unchecked
            return loader;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException(String.format(
                    "Could not instantiate loader type %s", loaderType), e);
        }
    }
}
