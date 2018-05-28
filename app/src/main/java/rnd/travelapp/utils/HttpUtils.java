package rnd.travelapp.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {
    public static <T> Failable<T> doGet(URL url, ConnectionProcessor<T> handler) {
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

    public static <T> Failable<T> doPost(URL url, ConnectionPoster poster, ConnectionProcessor<T> handler) {
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
    public interface ConnectionProcessor<T> {
        T process(HttpURLConnection connection, InputStream stream) throws Exception;
    }

    @FunctionalInterface
    public interface ConnectionPoster {
        void post(OutputStream outputStream) throws Exception;
    }
}
