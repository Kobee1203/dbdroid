package org.nds.dbdroid.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Is used to specify a mapped column for a persistent property or field. If no Column annotation is specified, the default values are applied.
 * 
 * @author ndossantos
 */
@Documented
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    /**
     * (Optional) The name of the column. Defaults to the property or field name.
     */
    String name() default "";
}
