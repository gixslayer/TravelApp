package rnd.travelapp.utils;

import java.util.function.Consumer;

public class CollectionUtils {
    public static <T> void forEach(T[] array, Consumer<T> consumer) {
        if(array != null) {
            for(T element : array) {
                consumer.accept(element);
            }
        }
    }
}
