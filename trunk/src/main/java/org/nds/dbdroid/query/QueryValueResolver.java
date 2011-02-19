package org.nds.dbdroid.query;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

public abstract class QueryValueResolver {

    public String toString(Object value) {
        String resultValue = null;
        if (value == null) {
            resultValue = toNullString(value);
        } else if (value instanceof String) {
            resultValue = toStringString((String) value);
        } else if (value instanceof Boolean) {
            resultValue = toBooleanString((Boolean) value);
        } else if (value instanceof Byte) {
            resultValue = toByteString((Byte) value);
        } else if (value instanceof Short) {
            resultValue = toShortString((Short) value);
        } else if (value instanceof Character) {
            resultValue = toCharacterString((Character) value);
        } else if (value instanceof Integer) {
            resultValue = toIntegerString((Integer) value);
        } else if (value instanceof Float) {
            resultValue = toFloatString((Float) value);
        } else if (value instanceof Long) {
            resultValue = toLongString((Long) value);
        } else if (value instanceof Double) {
            resultValue = toDoubleString((Double) value);
        } else if (value instanceof Number) {
            resultValue = toNumberString((Number) value);
        } else if (value instanceof Collection) {
            resultValue = toCollectionString((Collection<?>) value);
        } else if (value.getClass().isArray()) {
            int length = ArrayUtils.getLength(value);
            List<Object> list = new ArrayList<Object>();
            for (int i = 0; i < length; i++) {
                Object obj = Array.get(value, i);
                list.add(obj);
            }
            resultValue = toCollectionString(list);
        } else if (value instanceof Map) {
            resultValue = toMapString((Map<?, ?>) value);
        } else {
            resultValue = toObjectString(value);
        }
        return resultValue;
    }

    /**
     * Converts <code>null</code> value to a String
     * 
     * @param value
     *            : <code>null</code> value
     * @return <code>null</code> converted to String
     */
    protected abstract String toNullString(Object value);

    /**
     * Converts {@link String} value to a formatted String
     * 
     * @param value
     *            : {@link String} value
     * @return {@link String} converted to String
     */
    protected abstract String toStringString(String value);

    /**
     * Converts {@link Boolean} value to a String
     * 
     * @param value
     *            : {@link Boolean} value
     * @return {@link Boolean} converted to String
     */
    protected abstract String toBooleanString(Boolean value);

    /**
     * Converts {@link Byte} value to a String
     * 
     * @param value
     *            : {@link Byte} value
     * @return {@link Byte} converted to String
     */
    protected abstract String toByteString(Byte value);

    /**
     * Converts {@link Short} value to a String
     * 
     * @param value
     *            : {@link Short} value
     * @return {@link Short} converted to String
     */
    protected abstract String toShortString(Short value);

    /**
     * Converts {@link Character} value to a String
     * 
     * @param value
     *            : {@link Character} value
     * @return {@link Character} converted to String
     */
    protected abstract String toCharacterString(Character value);

    /**
     * Converts {@link Integer} value to a String
     * 
     * @param value
     *            : {@link Integer} value
     * @return {@link Integer} converted to String
     */
    protected abstract String toIntegerString(Integer value);

    /**
     * Converts {@link Float} value to a String
     * 
     * @param value
     *            : {@link Float} value
     * @return {@link Float} converted to String
     */
    protected abstract String toFloatString(Float value);

    /**
     * Converts {@link Long} value to a String
     * 
     * @param value
     *            : {@link Long} value
     * @return {@link Long} converted to String
     */
    protected abstract String toLongString(Long value);

    /**
     * Converts {@link Double} value to a String
     * 
     * @param value
     *            : {@link Double} value
     * @return {@link Double} converted to String
     */
    protected abstract String toDoubleString(Double value);

    /**
     * Converts {@link Number} value to a String
     * 
     * @param value
     *            : {@link Number} value
     * @return {@link Number} converted to String
     */
    protected abstract String toNumberString(Number value);

    /**
     * Converts {@link Collection} or objects array to a String.<br/>
     * If we have an objects array, this is converted to a collection before to be passed to this method
     * 
     * @param value
     *            : {@link Collection} or objects array
     * @return {@link Collection} or objects array converted to String
     */
    protected abstract String toCollectionString(Collection<?> values);

    /**
     * Converts {@link Map} value to a String
     * 
     * @param value
     *            : {@link Map} value
     * @return {@link Map} converted to String
     */
    protected abstract String toMapString(Map<?, ?> value);

    /**
     * Converts {@link Object} value to a String
     * 
     * @param value
     *            : {@link Object} value
     * @return {@link Object} converted to String
     */
    protected abstract String toObjectString(Object value);

}
