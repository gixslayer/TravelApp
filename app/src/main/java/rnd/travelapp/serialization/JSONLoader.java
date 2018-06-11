package rnd.travelapp.serialization;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Loads a type from a JSON object.
 * @param <T> the type to load
 */
public interface JSONLoader<T> {
    /**
     * Loads a type from the given JSON object.
     * @param object the JSON object to load from
     * @return the loaded type
     * @throws JSONException if the type could not be loaded
     */
    T deserialize(JSONObject object) throws JSONException;

    /**
     * Returns the type of the generic parameter, which is lost at runtime due to type erasure.
     * @return the type of generic parameter T
     */
    Class<T> getType();
}
