package org.nds.dbdroid.reflect.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.reflect.FieldUtils;

public final class AnnotationUtils {

    private AnnotationUtils() {
    }

    /**
     * Retrieve the default value of a named Annotation attribute, given the annotation type .
     * 
     * @param annotationType
     * @param attributeName
     * @return
     */
    public static Object getDefaultValue(Class<Annotation> annotationType, String attributeName) {
        try {
            Method method = annotationType.getDeclaredMethod(attributeName, new Class[0]);
            return method.getDefaultValue();
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Retrieve the value of a named Annotation attribute, given an annotation instance.
     * 
     * @param annotation
     * @param attributeName
     * @return
     */
    public static Object getValue(Annotation annotation, String attributeName) {
        try {
            Method method = annotation.annotationType().getDeclaredMethod(attributeName, new Class[0]);
            return method.invoke(annotation);
        } catch (Exception ex) {
            return null;
        }
    }

    public static <A extends Annotation> Object getFieldValue(Object object, Class<A> annotationType) {
        Field field = findField(object.getClass(), annotationType);
        try {
            return FieldUtils.readField(field, object, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <A extends Annotation> Map<Field, Object> getFieldsValues(Object object, Class<A> annotationType) {
        Field[] fields = findFields(object.getClass(), annotationType);

        Map<Field, Object> map = new HashMap<Field, Object>();
        for (Field field : fields) {
            try {
                map.put(field, FieldUtils.readField(field, object, true));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return map;
    }

    public static <A extends Annotation> Object getPropertyFieldValue(Object object, Class<A> annotationType) {
        Field field = findPropertyField(object.getClass(), annotationType);
        try {
            return FieldUtils.readField(field, object, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <A extends Annotation> String getPropertyFieldValueAsString(Object object, Class<A> annotationType) {
        Field field = findPropertyField(object.getClass(), annotationType);
        try {
            Object value = FieldUtils.readField(field, object, true);
            return ConvertUtils.convert(value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <A extends Annotation> Map<Field, Object> getPropertyFieldsValues(Object object, Class<A> annotationType) {
        Field[] fields = findPropertyFields(object.getClass(), annotationType);

        Map<Field, Object> map = new HashMap<Field, Object>();
        for (Field field : fields) {
            try {
                map.put(field, FieldUtils.readField(field, object, true));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return map;
    }

    public static <A extends Annotation> Map<Field, String> getPropertyFieldsValuesAsString(Object object, Class<A> annotationType) {
        Field[] fields = findPropertyFields(object.getClass(), annotationType);

        Map<Field, String> map = new HashMap<Field, String>();
        for (Field field : fields) {
            try {
                Object value = FieldUtils.readField(field, object, true);
                map.put(field, ConvertUtils.convert(value));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return map;
    }

    /**
     * Retrieve the given annotation's attributes as a Map.
     * 
     * @param annotation
     * @return
     */
    public static Map<String, Object> getAnnotationAttributes(Annotation annotation) {
        Map<String, Object> attrs = new HashMap<String, Object>();
        Method[] methods = annotation.annotationType().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getParameterTypes().length == 0 && method.getReturnType() != void.class) {
                try {
                    attrs.put(method.getName(), method.invoke(annotation));
                } catch (Exception ex) {
                    throw new IllegalStateException("Could not obtain annotation attribute values", ex);
                }
            }
        }
        return attrs;

    }

    public static <A extends Annotation> AnnotatedElement findMember(Class<?> clazz, Class<A> annotationType) {
        Class<?> cl = findClass(clazz, annotationType);
        if (cl != null) {
            return cl;
        }
        Constructor<?> constructor = findConstructor(clazz, annotationType);
        if (constructor != null) {
            return constructor;
        }
        Field field = findField(clazz, annotationType);
        if (field != null) {
            return field;
        }
        Method method = findMethod(clazz, annotationType);
        if (method != null) {
            return method;
        }

        return null;
    }

    public static <A extends Annotation> AnnotatedElement[] findMembers(Class<?> clazz, Class<A> annotationType) {
        List<AnnotatedElement> annotatedElements = new ArrayList<AnnotatedElement>();

        Class<?>[] cls = findClasses(clazz, annotationType);
        if (cls != null) {
            annotatedElements.addAll(Arrays.asList(cls));
        }
        Constructor<?>[] constructors = findConstructors(clazz, annotationType);
        if (constructors != null) {
            annotatedElements.addAll(Arrays.asList(constructors));
        }
        Field[] fields = findFields(clazz, annotationType);
        if (fields != null) {
            annotatedElements.addAll(Arrays.asList(fields));
        }
        Method[] methods = findMethods(clazz, annotationType);
        if (methods != null) {
            annotatedElements.addAll(Arrays.asList(methods));
        }

        return annotatedElements.toArray(new AnnotatedElement[] {});
    }

    ////////// CLASS //////////

    /**
     * Get a single Annotation of annotationType from the supplied class, traversing its interfaces and super classes if no annotation can be found on
     * the given class itself.
     * 
     * @param <A>
     * @param clazz
     * @param annotationType
     * @return
     */
    public static <A extends Annotation> A getAnnotation(Class<?> clazz, Class<A> annotationType) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz must not be null");
        }

        A annotation = clazz.getAnnotation(annotationType);
        if (annotation != null) {
            return annotation;
        }
        for (Class<?> ifc : clazz.getInterfaces()) {
            annotation = getAnnotation(ifc, annotationType);
            if (annotation != null) {
                return annotation;
            }
        }
        if (clazz.getSuperclass() == null || Object.class.equals(clazz.getSuperclass())) {
            return null;
        }

        return getAnnotation(clazz.getSuperclass(), annotationType);
    }

    /**
     * Get Annotations from the supplied class, traversing its interfaces and super classes if no annotation can be found on the given class itself.
     * 
     * @param clazz
     * @return
     */
    public static Annotation[] getAnnotations(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz must not be null");
        }

        Annotation[] annotations = clazz.getAnnotations();
        if (annotations != null) {
            return annotations;
        }
        for (Class<?> ifc : clazz.getInterfaces()) {
            annotations = getAnnotations(ifc);
            if (annotations != null) {
                return annotations;
            }
        }
        if (clazz.getSuperclass() == null || Object.class.equals(clazz.getSuperclass())) {
            return null;
        }

        return getAnnotations(clazz.getSuperclass());
    }

    public static <A extends Annotation> Class<?> findClass(Class<?> clazz, Class<A> annotationType) {
        A annotation = getAnnotation(clazz, annotationType);
        if (annotation != null) {
            return clazz;
        }
        Class<?>[] array = ReflectUtils.getInterfaces(clazz);
        for (Class<?> iface : array) {
            annotation = getAnnotation(iface, annotationType);
            if (annotation != null) {
                return iface;
            }
        }
        array = ReflectUtils.getSuperclasses(clazz);
        for (Class<?> superclass : array) {
            annotation = getAnnotation(superclass, annotationType);
            if (annotation != null) {
                return superclass;
            }
        }

        return null;
    }

    public static <A extends Annotation> Class<?>[] findClasses(Class<?> clazz, Class<A> annotationType) {
        List<Class<?>> classes = new ArrayList<Class<?>>();

        A annotation = getAnnotation(clazz, annotationType);
        if (annotation != null) {
            classes.add(clazz);
        }
        Class<?>[] array = ReflectUtils.getInterfaces(clazz);
        for (Class<?> iface : array) {
            annotation = getAnnotation(iface, annotationType);
            if (annotation != null) {
                classes.add(iface);
            }
        }
        array = ReflectUtils.getSuperclasses(clazz);
        for (Class<?> superclass : array) {
            annotation = getAnnotation(superclass, annotationType);
            if (annotation != null) {
                classes.add(superclass);
            }
        }

        return classes.toArray(new Class<?>[] {});
    }

    ////////// CONSTRUCTOR //////////

    /**
     * Get a single Annotation of annotationType from the supplied Constructor, traversing its super constructors if no annotation can be found on the
     * given constructor itself.
     * 
     * @param <A>
     * @param constructor
     * @param annotationType
     * @return
     */
    public static <A extends Annotation> A getAnnotation(Constructor<?> constructor, Class<A> annotationType) {
        if (constructor == null) {
            throw new IllegalArgumentException("constructor must not be null");
        }

        A annotation = constructor.getAnnotation(annotationType);
        Class<?> cl = constructor.getDeclaringClass();
        while (annotation == null) {
            cl = cl.getSuperclass();
            if (cl == null || cl.equals(Object.class)) {
                break;
            }
            try {
                Constructor<?> equivalentConstructor = cl.getDeclaredConstructor(constructor.getParameterTypes());
                annotation = equivalentConstructor.getAnnotation(annotationType);
            } catch (NoSuchMethodException ex) {
            }
        }

        return annotation;
    }

    /**
     * Get Annotations from the supplied Constructor, traversing its super constructors if no annotation can be found on the given constructor itself.
     * 
     * @param method
     * @return
     */
    public static Annotation[] getAnnotations(Constructor<?> constructor) {
        if (constructor == null) {
            throw new IllegalArgumentException("constructor must not be null");
        }

        Annotation[] annotations = constructor.getAnnotations();
        Class<?> cl = constructor.getDeclaringClass();
        while (annotations == null) {
            cl = cl.getSuperclass();
            if (cl == null || cl.equals(Object.class)) {
                break;
            }
            try {
                Constructor<?> equivalentConstructor = cl.getDeclaredConstructor(constructor.getParameterTypes());
                annotations = equivalentConstructor.getAnnotations();
            } catch (NoSuchMethodException ex) {
            }
        }

        return annotations;
    }

    public static <A extends Annotation> Constructor<?> findConstructor(Class<?> clazz, Class<A> annotationType) {
        Constructor<?>[] constructors = ReflectUtils.getConstructors(clazz);
        for (Constructor<?> constructor : constructors) {
            A annotation = getAnnotation(constructor, annotationType);
            if (annotation != null) {
                return constructor;
            }
        }

        return null;
    }

    public static <A extends Annotation> Constructor<?>[] findConstructors(Class<?> clazz, Class<A> annotationType) {
        List<Constructor<?>> constructors = new ArrayList<Constructor<?>>();

        Constructor<?>[] array = ReflectUtils.getConstructors(clazz);
        for (Constructor<?> constructor : array) {
            A annotation = getAnnotation(constructor, annotationType);
            if (annotation != null) {
                constructors.add(constructor);
            }
        }

        return constructors.toArray(new Constructor<?>[] {});
    }

    ////////// METHOD //////////

    /**
     * Get a single Annotation of annotationType from the supplied Method, traversing its super methods if no annotation can be found on the given
     * method itself.
     * 
     * @param <A>
     * @param method
     * @param annotationType
     * @return
     */
    public static <A extends Annotation> A getAnnotation(Method method, Class<A> annotationType) {
        if (method == null) {
            throw new IllegalArgumentException("method must not be null");
        }

        A annotation = method.getAnnotation(annotationType);
        Class<?> cl = method.getDeclaringClass();
        while (annotation == null) {
            cl = cl.getSuperclass();
            if (cl == null || cl.equals(Object.class)) {
                break;
            }
            try {
                Method equivalentMethod = cl.getDeclaredMethod(method.getName(), method.getParameterTypes());
                annotation = equivalentMethod.getAnnotation(annotationType);
            } catch (NoSuchMethodException ex) {
            }
        }

        return annotation;
    }

    /**
     * Get Annotations from the supplied Method, traversing its super methods if no annotation can be found on the given method itself.
     * 
     * @param method
     * @return
     */
    public static Annotation[] getAnnotations(Method method) {
        if (method == null) {
            throw new IllegalArgumentException("method must not be null");
        }

        Annotation[] annotations = method.getAnnotations();
        Class<?> cl = method.getDeclaringClass();
        while (annotations == null) {
            cl = cl.getSuperclass();
            if (cl == null || cl.equals(Object.class)) {
                break;
            }
            try {
                Method equivalentMethod = cl.getDeclaredMethod(method.getName(), method.getParameterTypes());
                annotations = equivalentMethod.getAnnotations();
            } catch (NoSuchMethodException ex) {
            }
        }

        return annotations;
    }

    public static <A extends Annotation> Method findMethod(Class<?> clazz, Class<A> annotationType) {
        Method[] methods = ReflectUtils.getMethods(clazz);
        for (Method method : methods) {
            A annotation = getAnnotation(method, annotationType);
            if (annotation != null) {
                return method;
            }
        }

        return null;
    }

    public static <A extends Annotation> Method[] findMethods(Class<?> clazz, Class<A> annotationType) {
        List<Method> methods = new ArrayList<Method>();

        Method[] array = ReflectUtils.getMethods(clazz);
        for (Method method : array) {
            A annotation = getAnnotation(method, annotationType);
            if (annotation != null) {
                methods.add(method);
            }
        }

        return methods.toArray(new Method[] {});
    }

    ////////// FIELD //////////

    /**
     * Get a single Annotation of annotationType from the supplied Field, traversing its super fields if no annotation can be found on the given field
     * itself.
     * 
     * @param <A>
     * @param method
     * @param annotationType
     * @return
     */
    public static <A extends Annotation> A getAnnotation(Field field, Class<A> annotationType) {
        if (field == null) {
            throw new IllegalArgumentException("field must not be null");
        }

        A annotation = field.getAnnotation(annotationType);
        Class<?> cl = field.getDeclaringClass();
        while (annotation == null) {
            cl = cl.getSuperclass();
            if (cl == null || cl.equals(Object.class)) {
                break;
            }
            try {
                Field equivalentField = cl.getDeclaredField(field.getName());
                annotation = equivalentField.getAnnotation(annotationType);
            } catch (SecurityException e) {
            } catch (NoSuchFieldException e) {
            }
        }

        return annotation;
    }

    /**
     * Get Annotations from the supplied Field, traversing its super fields if no annotation can be found on the given field itself.
     * 
     * @param method
     * @return
     */
    public static Annotation[] getAnnotations(Field field) {
        if (field == null) {
            throw new IllegalArgumentException("field must not be null");
        }

        Annotation[] annotations = field.getAnnotations();
        Class<?> cl = field.getDeclaringClass();
        while (annotations == null) {
            cl = cl.getSuperclass();
            if (cl == null || cl.equals(Object.class)) {
                break;
            }
            try {
                Field equivalentField = cl.getDeclaredField(field.getName());
                annotations = equivalentField.getAnnotations();
            } catch (SecurityException e) {
            } catch (NoSuchFieldException e) {
            }
        }

        return annotations;
    }

    public static <A extends Annotation> Field findField(Class<?> clazz, Class<A> annotationType) {
        Field[] fields = ReflectUtils.getFields(clazz);
        for (Field field : fields) {
            A annotation = getAnnotation(field, annotationType);
            if (annotation != null) {
                return field;
            }
        }

        return null;
    }

    public static <A extends Annotation> Field[] findFields(Class<?> clazz, Class<A> annotationType) {
        List<Field> fields = new ArrayList<Field>();

        Field[] array = ReflectUtils.getFields(clazz);
        for (Field field : array) {
            A annotation = getAnnotation(field, annotationType);
            if (annotation != null) {
                fields.add(field);
            }
        }

        return fields.toArray(new Field[] {});
    }

    public static <A extends Annotation> Field findPropertyField(Class<?> clazz, Class<A> annotationType) {
        Field[] fields = ReflectUtils.getPropertyFields(clazz);
        for (Field field : fields) {
            A annotation = getAnnotation(field, annotationType);
            if (annotation != null) {
                return field;
            }
        }

        return null;
    }

    public static <A extends Annotation> Field[] findPropertyFields(Class<?> clazz, Class<A> annotationType) {
        List<Field> fields = new ArrayList<Field>();

        Field[] array = ReflectUtils.getPropertyFields(clazz);
        for (Field field : array) {
            A annotation = getAnnotation(field, annotationType);
            if (annotation != null) {
                fields.add(field);
            }
        }

        return fields.toArray(new Field[] {});
    }

}
