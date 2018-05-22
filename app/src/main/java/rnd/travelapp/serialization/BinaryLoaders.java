package rnd.travelapp.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import rnd.travelapp.resources.Resource;

public class BinaryLoaders {
    private static final Map<Class, BinaryLoader> LOADERS = new HashMap<>();

    public static <T> T deserialize(InputStream stream, Class<? extends Resource<T>> type) throws IOException {
        BinaryLoader<T> loader = get(type);

        return loader.deserialize(stream);
    }

    public static <T> BinaryLoader<T> get(Resource<T> instance) {
        //noinspection unchecked
        return get((Class<? extends Resource<T>>)instance.getClass());
    }

    public static <T> BinaryLoader<T> get(Class<? extends Resource<T>> type) {
        if(!LOADERS.containsKey(type)) {
            LOADERS.put(type, create(type));
        }

        //noinspection unchecked
        return LOADERS.get(type);
    }

    public static <T> Class<T> getType(Class<? extends Resource<T>> type) {
        return get(type).getType();
    }

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
