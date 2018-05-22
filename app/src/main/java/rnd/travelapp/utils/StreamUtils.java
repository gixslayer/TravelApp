package rnd.travelapp.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;

public class StreamUtils {
    public static String toString(InputStream stream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        processStream(stream, outputStream::write);

        return outputStream.toString("UTF-8");
    }

    public static byte[] checksum(InputStream stream, MessageDigest digest) throws IOException {
        processStream(stream, digest::update);

        return digest.digest();
    }

    public static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        processStream(inputStream, outputStream::write);
    }

    private static void processStream(InputStream stream, StreamProcessor processor) throws IOException {
        byte[] buffer = new byte[4096];
        int bytesRead;

        do {
            bytesRead = stream.read(buffer);

            if(bytesRead > 0) {
                processor.process(buffer, 0, bytesRead);
            }
        } while(bytesRead == buffer.length);
    }

    public interface StreamProcessor {
        void process(byte[] buffer, int offset, int length) throws IOException;
    }
}
