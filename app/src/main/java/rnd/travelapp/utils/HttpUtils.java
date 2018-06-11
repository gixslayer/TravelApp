package rnd.travelapp.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Utility methods related to HTTP interaction.
 */
public class HttpUtils {
    /**
     * Perform a HTTP GET operation.
     * @param url the url to GET
     * @param handler the handler to invoke once the connection is established
     * @param <T> the return type of the handler
     * @return the result of the handler, which could fail
     */
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

    /**
     * Perform a HTTP POST operation.
     * @param url the url to POST
     * @param poster the poster to invoke to perform the POST once the connection is established
     * @param handler the handler to invoke once the server has responded
     * @param <T> the return type of the handler
     * @return the result of the handler, which could fail
     */
    public static <T> Failable<T> doPost(URL url, ConnectionPoster poster, ConnectionProcessor<T> handler) {
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setChunkedStreamingMode(0);

            OutputStream outputStream = new BufferedOutputStream(connection.getOutputStream());
            poster.post(outputStream);

            outputStream.flush();

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

    /**
     * Defines the HTTP GET handler interface.
     * @param <T> the return type of the interface
     */
    @FunctionalInterface
    public interface ConnectionProcessor<T> {
        /**
         * Performs the GET operation on the HTTP connection.
         * @param connection the HTTP connection
         * @param stream the stream to GET from
         * @return the processed GET response
         * @throws Exception if the GET failed
         */
        T process(HttpURLConnection connection, InputStream stream) throws Exception;
    }

    /**
     * Defines the HTTP POST poster interface.
     */
    @FunctionalInterface
    public interface ConnectionPoster {
        /**
         * Performs the POST operation on the HTTP connection.
         * @param outputStream the stream to POST to
         * @throws Exception if the POST failed
         */
        void post(OutputStream outputStream) throws Exception;
    }
}
