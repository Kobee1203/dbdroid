package org.nds.dbdroid.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that the class is an entity. This annotation is applied to the entity class.
 * 
 * @author ndossantos
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {

    /**
     * (Optional) The entity name. Defaults to the unqualified name of the entity class. This name is used to refer to the entity in queries.
     */
    String name() default "";
}
