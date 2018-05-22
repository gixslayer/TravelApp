package rnd.travelapp.serialization;

import java.io.IOException;
import java.io.InputStream;

import rnd.travelapp.resources.Resource;

public interface BinaryLoader<T> {
    T deserialize(InputStream stream) throws IOException;
    Resource<T> create(String path, T instance);
    Class<T> getType();
}
