package rnd.travelapp.serialization;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper class to create easy cached access to JSON loaders for a given type.
 */
public class JSONLoaders {
    private static final Map<Class, JSONLoader> LOADERS = new HashMap<>();

    /**
     * Deserialize an instance of the given type from the given JSON string.
     * @param json the JSON object string to load from
     * @param type the type to load
     * @param <T> the type to load
     * @return the loaded type
     * @throws JSONException if the loading failed
     */
    public static <T> T deserialize(String json, Class<T> type) throws JSONException {
        return get(type).deserialize(new JSONObject(json));
    }

    /**
     * Returns the JSON loader for the given instance.
     * @param instance the instance
     * @param <T> the instance type
     * @return the loader instance
     */
    public static <T> JSONLoader<T> get(T instance) {
        //noinspection unchecked
        return get((Class<T>)instance.getClass());
    }

    /**
     * Returns the JSON loader for the given type.
     * @param type the type
     * @param <T> the type
     * @return the loader instance
     */
    public static <T> JSONLoader<T> get(Class<T> type) {
        synchronized (LOADERS) {
            //noinspection unchecked
            return LOADERS.computeIfAbsent(type, JSONLoaders::create);
        }
    }

    /**
     * Creates a new JSON loader for the given type.
     * @param type the type
     * @param <T> the type
     * @return the loader instance
     */
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
