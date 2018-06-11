package rnd.travelapp.serialization;

import java.io.IOException;
import java.io.InputStream;

import rnd.travelapp.resources.Resource;

/**
 * Loads a type from a binary stream.
 * @param <T> the type to load
 */
public interface BinaryLoader<T> {
    /**
     * Loads a type from the given input stream.
     * @param stream the stream to load from
     * @return the loaded type
     * @throws IOException if the type could not be loaded
     */
    T deserialize(InputStream stream) throws IOException;

    /**
     * Creates a new Resource wrapper instance.
     * @param path the path of the resource
     * @param instance the resource instance
     * @return the created wrapper instance
     */
    Resource<T> create(String path, T instance);

    /**
     * Returns the type of the generic parameter, which is lost at runtime due to type erasure.
     * @return the type of generic parameter T
     */
    Class<T> getType();
}
