package rnd.travelapp;

import android.content.Context;
import android.content.res.Resources;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import rnd.travelapp.threading.FailableTask;
import rnd.travelapp.threading.Task;
import rnd.travelapp.utils.HttpUtils;
import rnd.travelapp.utils.StreamUtils;

public class Booking {
    private final URL bookURL;
    private final String reis;
    private final String voornaam;
    private final String achternaam;
    private final String gebdatum;
    private final String straatHuisNr;
    private final String postcode;
    private final String stad;
    private final String telefoon;
    private final String email;

    public Booking(Context context, String reis, String voornaam, String achternaam, String gebdatum,
                   String straatHuisNr, String postcode, String stad, String telefoon, String email) {
        this.bookURL = createURL(context);
        this.reis = reis;
        this.voornaam = voornaam;
        this.achternaam = achternaam;
        this.gebdatum = gebdatum;
        this.straatHuisNr = straatHuisNr;
        this.postcode = postcode;
        this.stad = stad;
        this.telefoon = telefoon;
        this.email = email;
    }

    public FailableTask<Integer> send() {
        return Task.createFailable(() -> HttpUtils.doPost(bookURL, this::postData, this::parseResponse));
    }

    private void postData(OutputStream stream) throws IOException, JSONException {
        JSONObject object = new JSONObject();

        object.put("reis", reis);
        object.put("voornaam", voornaam);
        object.put("achternaam", achternaam);
        object.put("gebdatum", gebdatum);
        object.put("straat_huisnr", straatHuisNr);
        object.put("postcode", postcode);
        object.put("stad", stad);
        object.put("telefoon", telefoon);
        object.put("email", email);

        String json = object.toString();
        byte[] data = json.getBytes(StandardCharsets.UTF_8);

        stream.write(data);
    }

    private int parseResponse(HttpURLConnection connection, InputStream stream) throws IOException {
        return Integer.parseInt(StreamUtils.toString(stream));
    }

    private static URL createURL(Context context) {
        Resources resources = context.getResources();
        String serverHost = resources.getString(R.string.server_host);
        int serverPort = resources.getInteger(R.integer.server_port);

        try {
            return new URL("http", serverHost, serverPort, "book");
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not create booking url");
        }
    }
}
