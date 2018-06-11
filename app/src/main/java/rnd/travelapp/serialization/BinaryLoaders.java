package rnd.travelapp.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import rnd.travelapp.resources.Resource;

/**
 * Wrapper class to create easy cached access to binary loaders for a given type.
 */
public class BinaryLoaders {
    private static final Map<Class, BinaryLoader> LOADERS = new HashMap<>();

    /**
     * Deserialize an instance of the given wrapped resource type from the given stream.
     * @param stream the stream to load from
     * @param type the wrapped resource type to load
     * @param <T> the unwrapped resource type
     * @return the loaded unwrapped resource type
     * @throws IOException if the loading failed
     */
    public static <T> T deserialize(InputStream stream, Class<? extends Resource<T>> type) throws IOException {
        return get(type).deserialize(stream);
    }

    /**
     * Returns the binary loader for the given wrapped resource instance.
     * @param instance the wrapper resource instance
     * @param <T> the unwrapped resource type
     * @return the loader instance
     */
    public static <T> BinaryLoader<T> get(Resource<T> instance) {
        //noinspection unchecked
        return get((Class<? extends Resource<T>>)instance.getClass());
    }

    /**
     * Returns the binary loader for the given wrapped resource type.
     * @param type the wrapped resource type
     * @param <T> the unwrapped resource type
     * @return the loader instance
     */
    public static <T> BinaryLoader<T> get(Class<? extends Resource<T>> type) {
        synchronized (LOADERS) {
            //noinspection unchecked
            return LOADERS.computeIfAbsent(type, BinaryLoaders::create);
        }
    }

    /**
     * Returns the type of the unwrapped resource type from the wrapped resource type. This method is
     * required as this information is normally lost at runtime due to type erasure.
     * @param type the wrapped resource type
     * @param <T> the unwrapped resource type
     * @return the unwrapped resource type T
     */
    public static <T> Class<T> getType(Class<? extends Resource<T>> type) {
        return get(type).getType();
    }

    /**
     * Creates a new binary loader for the given wrapped resource type.
     * @param type the wrapped resource type
     * @param <T> the unwrapped resource type
     * @return the loader instance
     */
    private static <T> BinaryLoader<T> create(Class<? extends Resource<T>> type) {
        if(!type.isAnnotationPresent(BinaryLoadable.class)) {
            throw new IllegalArgumentException(String.format(
                    "type %s is missing BinaryLoadable attribute", type));
        }

        BinaryLoadable loaderInfo = type.getAnnotation(BinaryLoadable.class);
        Class<? extends BinaryLoader> loaderType = loaderInfo.value();

        if(loaderType.isMemberClass() && !Modifier.isStatic(loaderType.getModifiers())) {
            throw new IllegalArgumentException(String.format(
                    "Loader type %s must be static because it is a member class", loaderType));
        }

        try {
            BinaryLoader loader = loaderType.newInstance();
            Resource resource = loader.create(null, null);
            Class<?> loadType = loader.getType();
            Class<?> resourceType = resource.getResourceType();

            if(!resourceType.equals(loadType)) {
                throw new IllegalArgumentException(String.format(
                        "Resource type %s does not match BinaryLoader type %s", resourceType, loadType));
            }

            //noinspection unchecked
            return loader;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException(String.format(
                    "Could not instantiate loader type %s", loaderType), e);
        }
    }
}
