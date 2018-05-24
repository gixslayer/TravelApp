package rnd.travelapp.cache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import rnd.travelapp.utils.Failable;
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
        return doGet(hostURL, (connection, stream) -> StreamUtils.toString(stream));
    }

    @Override
    public Failable<List<ValidatorEntry>> validate(List<DiskCacheEntry> entries) {
        return doPost(hostURL,
                stream -> postEntries(stream, entries),
                (connection, stream) -> parseEntries(stream));
    }

    private void postEntries(OutputStream stream, List<DiskCacheEntry> entries) {

    }

    private List<ValidatorEntry> parseEntries(InputStream stream) {
        return null;
    }

    private <T> Failable<T> doGet(URL url, ConnectionHandler<T> handler) {
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();

            InputStream inputStream = new BufferedInputStream(connection.getInputStream());
            T result = handler.process(connection, inputStream);

            return Failable.success(result);
        } catch (Exception e) {
            return Failable.failure(e);
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
    }

    private <T> Failable<T> doPost(URL url, ConnectionPoster poster, ConnectionHandler<T> handler) {
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setChunkedStreamingMode(0);

            OutputStream outputStream = new BufferedOutputStream(connection.getOutputStream());
            poster.post(outputStream);

            InputStream inputStream = new BufferedInputStream(connection.getInputStream());
            T result = handler.process(connection, inputStream);

            return Failable.success(result);
        } catch (Exception e) {
            return Failable.failure(e);
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
    }

    @FunctionalInterface
    private interface ConnectionHandler<T> {
        T process(HttpURLConnection connection, InputStream stream) throws Exception;
    }

    @FunctionalInterface
    private interface ConnectionPoster {
        void post(OutputStream outputStream) throws Exception;
    }
}
