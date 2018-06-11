package rnd.travelapp.serialization;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public interface JSONLoader<T> {
    T deserialize(JSONObject object) throws JSONException;
    Class<T> getType();

    default String jsonArrayToParagraph(JSONArray array) throws JSONException {
        StringBuilder sb = new StringBuilder();
        if (array != null && array.length() > 0)
            for (int i = 0; i < array.length(); i++)
                sb.append(array.getString(i)).append("\n\n");
        return sb.toString();
    }
}
