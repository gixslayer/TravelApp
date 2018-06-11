package rnd.travelapp.utils;

import java.util.function.Consumer;

/**
 * Utility methods related to collections.
 */
public class CollectionUtils {
    /**
     * Perform an operation on each element of an array. If the array is null, no operations are performed.
     * @param array the array to iterate over
     * @param consumer the operation to perform on each element
     * @param <T> the array element type
     */
    public static <T> void forEach(T[] array, Consumer<T> consumer) {
        if(array != null) {
            for(T element : array) {
                consumer.accept(element);
            }
        }
    }
}
