package org.nds.dbdroid.helper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.apache.commons.lang.reflect.FieldUtils;
import org.nds.dbdroid.annotation.Column;
import org.nds.dbdroid.annotation.Entity;
import org.nds.dbdroid.annotation.Id;
import org.nds.dbdroid.reflect.utils.AnnotationUtils;
import org.nds.dbdroid.reflect.utils.ReflectUtils;
import org.nds.logging.Logger;
import org.nds.logging.LoggerFactory;

public final class EntityHelper {

    private static final Logger log = LoggerFactory.getLogger(EntityHelper.class);

    private EntityHelper() {
    }

    public static String getTableName(Class<?> entityClass) {
        Entity entity = AnnotationUtils.getAnnotation(entityClass, Entity.class);
        String tableName = entity.name();
        if (tableName == null) {
            throw new IllegalArgumentException("Table name is NULL for Entity " + entityClass.getClass().getCanonicalName());
        } else if ("".equals(tableName)) {
            tableName = entityClass.getSimpleName().toUpperCase();
            log.debug("Table name is empty -> Define table with lowercase Entity class name: " + tableName);
        }

        return tableName;
    }

    public static Field[] getFields(Class<?> entityClass) {
        return ReflectUtils.getPropertyFields(entityClass);
    }

    public static Map<String, Object> getColumnNamesWithValues(Object entity) {
        Map<String, Object> map = new HashMap<String, Object>();

        Field[] fields = getFields(entity.getClass());
        for (Field field : fields) {
            String columnName = getColumnName(field);
            Object value = readField(field, entity);
            if (isIdField(field) && value == null) { // Don't store id field with value is NULL
                continue;
            }
            map.put(columnName, value);
        }

        return map;
    }

    public static Field getIdField(Class<?> entityClass) {
        // Retrieve the ID field
        java.lang.reflect.Field field = AnnotationUtils.findField(entityClass, Id.class);
        if (field == null) {
            field = FieldUtils.getField(entityClass, "_id", true);
            if (field == null) {
                throw new IllegalArgumentException("No fields are found with the 'Id' annotation or '_id' name.");
            }
        }

        return field;
    }

    public static boolean isIdField(Field field) {
        Annotation idAnnotation = AnnotationUtils.getAnnotation(field, Id.class);
        return idAnnotation != null;
    }

    public static Field getFieldByColumnName(String columnName, Class<?> entityClass) {
        // Search a field with a Column annotation where name is columnName
        Field[] fields = AnnotationUtils.findFields(entityClass, Column.class);
        for (Field field : fields) {
            Annotation[] annotations = AnnotationUtils.getAnnotations(field);
            for (Annotation annotation : annotations) {
                // Retrieve the column name is defined 
                if (Column.class.equals(annotation.annotationType())) {
                    String colName = ((Column) annotation).name();
                    if (columnName.equals(colName)) {
                        return field;
                    }
                }
            }
        }

        // Search a field where field name is columnName
        return FieldUtils.getField(entityClass, columnName, true);
    }

    public static String getColumnName(Field field) {
        String columnName = null;

        if (field != null) {
            Annotation[] annotations = AnnotationUtils.getAnnotations(field);
            for (Annotation annotation : annotations) {
                // Retrieve the column name is defined 
                if (Column.class.equals(annotation.annotationType())) {
                    columnName = ((Column) annotation).name();
                }
            }
            if (columnName == null) {
                // Set the column name if Column annotation not defined
                columnName = field.getName().toLowerCase();
            }
        }

        return columnName;
    }

    public static void writeField(Field field, Object value, Object entity) {
        try {
            Object v = DefaultTypeConverter.INSTANCE.convert(field.getType(), value);
            FieldUtils.writeField(field, entity, v, true);
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static Object readField(Field field, Object entity) {
        Object value = null;
        try {
            value = FieldUtils.readField(field, entity, true);
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        }
        return value;
    }

    public static <T> T readField(Field field, Object entity, Class<T> type) {
        Object value = readField(field, entity);
        return DefaultTypeConverter.INSTANCE.convert(type, value);
    }
}
