package rnd.travelapp.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;

/**
 * Utility methods related to stream interaction.
 */
public class StreamUtils {
    /**
     * Converts an input stream to a String by reading it as an UTF-8 byte sequence.
     * @param stream the stream to convert from
     * @return the resulting String
     * @throws IOException if the conversion failed
     */
    public static String toString(InputStream stream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        processStream(stream, outputStream::write);

        return outputStream.toString("UTF-8");
    }

    /**
     * Computes the checksum over a stream.
     * @param stream the stream to compute a checksum from
     * @param digest the digest to use for the checksum
     * @return the checksum
     * @throws IOException if the computation failed
     */
    public static byte[] checksum(InputStream stream, MessageDigest digest) throws IOException {
        processStream(stream, digest::update);

        return digest.digest();
    }

    /**
     * Copies a stream.
     * @param inputStream the stream to copy from
     * @param outputStream the stream to copy to
     * @throws IOException if the copy failed
     */
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
        } while(bytesRead > 0);
    }

    public interface StreamProcessor {
        void process(byte[] buffer, int offset, int length) throws IOException;
    }
}
