package rnd.travelapp.cache;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import rnd.travelapp.utils.Failable;
import rnd.travelapp.utils.StreamUtils;

public class HttpFetcher implements Fetcher {
    private final String hostURL;
    private final int hostPort;

    public HttpFetcher(String hostURL, int hostPort) {
        this.hostURL = hostURL;
        this.hostPort = hostPort;
    }

    private URL getFetchURL(String key) throws MalformedURLException {
        return new URL("http", hostURL, hostPort, "files/" + key);
    }

    @Override
    public Failable<File> fetch(String key, File destination) {
        HttpURLConnection connection = null;

        destination.getParentFile().mkdirs();

        try {
            connection = (HttpURLConnection)getFetchURL(key).openConnection();
            OutputStream outputStream = new FileOutputStream(destination);
            InputStream inputStream = new BufferedInputStream(connection.getInputStream());

            StreamUtils.copy(inputStream, outputStream);

            return Failable.success(destination);
        } catch (IOException e){
            return Failable.failure(e);
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
    }
}
