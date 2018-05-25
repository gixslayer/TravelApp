package rnd.travelapp.cache;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import rnd.travelapp.utils.Failable;
import rnd.travelapp.utils.HttpUtils;
import rnd.travelapp.utils.StreamUtils;

public class HttpValidator implements Validator {
    private final URL hostURL;

    public HttpValidator(String host, int port) {
        try {
            this.hostURL = new URL("http", host, port, "verify");
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed host/port", e);
        }
    }

    @Override
    public Failable<String> getLastModified() {
        return HttpUtils.doGet(hostURL, (connection, stream) -> StreamUtils.toString(stream));
    }

    @Override
    public Failable<List<ValidatorEntry>> validate(List<DiskCacheEntry> entries) {
        return HttpUtils.doPost(hostURL,
                stream -> postEntries(stream, entries),
                (connection, stream) -> parseEntries(stream));
    }

    private void postEntries(OutputStream stream, List<DiskCacheEntry> entries) throws JSONException, IOException {
        JSONArray array = new JSONArray();

        entries.forEach(e -> array.put(createPostEntry(e)));

        String json = array.toString(0);
        stream.write(json.getBytes("UTF-8"));
    }

    private JSONObject createPostEntry(DiskCacheEntry entry) {
        JSONObject object = new JSONObject();

        try {
            object.put("file", entry.getPath());
            object.put("checksum", entry.getChecksum());
        } catch (JSONException e) {
            // NOTE: This exception can only be thrown when an invalid Number object is passed, thus
            // never in this case.
        }

        return object;
    }

    private List<ValidatorEntry> parseEntries(InputStream stream) throws IOException, JSONException {
        String json = StreamUtils.toString(stream);
        JSONArray array = new JSONArray(json);
        List<ValidatorEntry> entries = new ArrayList<>();

        for(int i = 0; i < array.length(); ++i) {
            JSONObject object = array.getJSONObject(i);

            entries.add(ValidatorEntry.fromJSON(object));
        }

        return entries;
    }
}
