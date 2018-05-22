package rnd.travelapp.serialization;

import org.json.JSONException;
import org.json.JSONObject;

public interface JSONLoader<T> {
    T deserialize(JSONObject object) throws JSONException;
    Class<T> getType();
}
