package org.nds.dbdroid.reflect.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.reflect.MethodUtils;

public final class ReflectUtils {

    private static final String PREFIX_IS = "is";
    private static final String PREFIX_GET = "get";
    private static final String PREFIX_SET = "set";

    private ReflectUtils() {
    }

    /**
     * <p>
     * Capitalizes a String changing the first letter to title case as per {@link Character#toTitleCase(char)}. No other letters are changed.
     * </p>
     * 
     * @param str
     *            the String to capitalize, may be null
     * @return the capitalized String, <code>null</code> if null String input
     * @see #uncapitalize(String)
     */
    public static String capitalize(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        return new StringBuffer(strLen).append(Character.toTitleCase(str.charAt(0))).append(str.substring(1)).toString();
    }

    /**
     * <p>
     * Uncapitalizes a String changing the first letter to title case as per {@link Character#toLowerCase(char)}. No other letters are changed.
     * </p>
     * 
     * @param str
     *            the String to uncapitalize, may be null
     * @return the uncapitalized String, <code>null</code> if null String input
     * @see #capitalize(String)
     */
    public static String uncapitalize(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        return new StringBuffer(strLen).append(Character.toLowerCase(str.charAt(0))).append(str.substring(1)).toString();
    }

    public static boolean isGetter(Method method) {
        if (method == null) {
            return false;
        }
        String name = method.getName();
        Class<?> type = method.getReturnType();
        Class<?> params[] = method.getParameterTypes();

        // special for isXXX boolean
        if (name.startsWith(PREFIX_IS) && name.length() > 2) {
            return params.length == 0 && type.getSimpleName().equalsIgnoreCase("boolean");
        }

        return name.startsWith(PREFIX_GET) && name.length() > 3 && params.length == 0 && !type.equals(Void.TYPE);
    }

    public static boolean isSetter(Method method) {
        if (method == null) {
            return false;
        }
        String name = method.getName();
        Class<?> type = method.getReturnType();
        Class<?> params[] = method.getParameterTypes();

        return name.startsWith(PREFIX_SET) && name.length() > 3 && params.length == 1 && type.equals(Void.TYPE);
    }

    public static Field[] getFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<Field>();
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        if (clazz.getSuperclass() != null) {
            fields.addAll(Arrays.asList(getFields(clazz.getSuperclass())));
        }

        return fields.toArray(new Field[] {});
    }

    public static Field[] getPropertyFields(Class<?> clazz) {
        List<Field> propertyFields = new ArrayList<Field>();

        Field[] fields = getFields(clazz);
        for (Field field : fields) {
            Class<?> fieldClass = field.getDeclaringClass();
            String fieldName = field.getName();
            Class<?> type = field.getType();

            Method setterMethod = MethodUtils.getMatchingAccessibleMethod(fieldClass, PREFIX_SET + capitalize(fieldName), new Class[] { type });

            Method getterMethod;
            // special for isXXX boolean
            if (type.getSimpleName().equalsIgnoreCase("boolean")) {
                getterMethod = MethodUtils.getMatchingAccessibleMethod(fieldClass, PREFIX_IS + capitalize(fieldName), (Class[]) null);
            } else {
                getterMethod = MethodUtils.getMatchingAccessibleMethod(fieldClass, PREFIX_GET + capitalize(fieldName), (Class[]) null);
            }
            if (isGetter(getterMethod) && isSetter(setterMethod)) {
                propertyFields.add(field);
            }
        }

        return propertyFields.toArray(new Field[] {});
    }

    public static Method[] getMethods(Class<?> clazz) {
        List<Method> methods = new ArrayList<Method>();
        methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
        if (clazz.getSuperclass() != null) {
            methods.addAll(Arrays.asList(getMethods(clazz.getSuperclass())));
        }

        return methods.toArray(new Method[] {});
    }

    public static Constructor<?>[] getConstructors(Class<?> clazz) {
        List<Constructor<?>> constructor = new ArrayList<Constructor<?>>();

        for (Constructor<?> constr : clazz.getDeclaredConstructors()) {
            constructor.add(constr);
        }
        if (clazz.getSuperclass() != null) {
            constructor.addAll(Arrays.asList(getConstructors(clazz.getSuperclass())));
        }

        return constructor.toArray(new Constructor<?>[] {});
    }

    public static Class<?>[] getInterfaces(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz must not be null");
        }

        Set<Class<?>> interfaces = new LinkedHashSet<Class<?>>();

        if (clazz.getInterfaces() != null) {
            for (Class<?> iface : clazz.getInterfaces()) {
                interfaces.add(iface);
            }
            for (Class<?> i : interfaces) {
                interfaces.addAll(Arrays.asList(getInterfaces(i)));
            }
        }

        return interfaces.toArray(new Class<?>[] {});
    }

    public static Class<?>[] getSuperclasses(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz must not be null");
        }

        List<Class<?>> superclasses = new ArrayList<Class<?>>();
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) {
            superclasses.add(superclass);
            superclasses.addAll(Arrays.asList(getSuperclasses(superclass)));
        }

        return superclasses.toArray(new Class<?>[] {});
    }

}
