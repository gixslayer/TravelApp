package rnd.travelapp.serialization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the BinaryLoader to use while loading the decorated class.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BinaryLoadable {
    Class<? extends BinaryLoader> value();
}
