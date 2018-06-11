package rnd.travelapp.cache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import rnd.travelapp.utils.Failable;
import rnd.travelapp.utils.HttpUtils;
import rnd.travelapp.utils.StreamUtils;

/**
 * Fetcher implementation that fetches from a remote HTTP server.
 */
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
        destination.getParentFile().mkdirs();
        URL fetchURL;

        try {
            fetchURL = getFetchURL(key);
        }  catch (MalformedURLException e) {
            return Failable.failure(e);
        }

        return HttpUtils.doGet(fetchURL, ((connection, stream) -> {
            OutputStream outputStream = new FileOutputStream(destination);

            StreamUtils.copy(stream, outputStream);

            return destination;
        }));
    }
}
