package org.nds.dbdroid.type;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class DataType {

    private static final Map<String, DbDroidType> DATA_TYPES;

    static {
        HashMap<String, DbDroidType> basics = new HashMap<String, DbDroidType>();
        basics.put(boolean.class.getName(), DbDroidType.BOOLEAN);
        basics.put(long.class.getName(), DbDroidType.LONG);
        basics.put(short.class.getName(), DbDroidType.SHORT);
        basics.put(int.class.getName(), DbDroidType.INTEGER);
        basics.put(byte.class.getName(), DbDroidType.BYTE);
        basics.put(float.class.getName(), DbDroidType.FLOAT);
        basics.put(double.class.getName(), DbDroidType.DOUBLE);
        basics.put(char.class.getName(), DbDroidType.CHARACTER);
        basics.put(Boolean.class.getName(), DbDroidType.BOOLEAN);
        basics.put(Long.class.getName(), DbDroidType.LONG);
        basics.put(Short.class.getName(), DbDroidType.SHORT);
        basics.put(Integer.class.getName(), DbDroidType.INTEGER);
        basics.put(Byte.class.getName(), DbDroidType.BYTE);
        basics.put(Float.class.getName(), DbDroidType.FLOAT);
        basics.put(Double.class.getName(), DbDroidType.DOUBLE);
        basics.put(Character.class.getName(), DbDroidType.CHARACTER);
        basics.put(String.class.getName(), DbDroidType.STRING);
        basics.put(java.util.Date.class.getName(), DbDroidType.TIMESTAMP);
        basics.put(Time.class.getName(), DbDroidType.TIME);
        basics.put(Timestamp.class.getName(), DbDroidType.TIMESTAMP);
        basics.put(java.sql.Date.class.getName(), DbDroidType.DATE);
        basics.put(BigDecimal.class.getName(), DbDroidType.BIG_DECIMAL);
        basics.put(BigInteger.class.getName(), DbDroidType.BIG_INTEGER);
        basics.put(Locale.class.getName(), DbDroidType.LOCALE);
        basics.put(Calendar.class.getName(), DbDroidType.CALENDAR);
        basics.put(GregorianCalendar.class.getName(), DbDroidType.CALENDAR);
        basics.put(TimeZone.class.getName(), DbDroidType.TIMEZONE);
        basics.put(Object.class.getName(), DbDroidType.OBJECT);
        basics.put(Class.class.getName(), DbDroidType.CLASS);
        basics.put(byte[].class.getName(), DbDroidType.BINARY);
        basics.put("byte[]", DbDroidType.BINARY);
        basics.put(Byte[].class.getName(), DbDroidType.WRAPPER_BINARY);
        basics.put("Byte[]", DbDroidType.WRAPPER_BINARY);
        basics.put(char[].class.getName(), DbDroidType.CHAR_ARRAY);
        basics.put("char[]", DbDroidType.CHAR_ARRAY);
        basics.put(Character[].class.getName(), DbDroidType.CHARACTER_ARRAY);
        basics.put("Character[]", DbDroidType.CHARACTER_ARRAY);
        basics.put(Blob.class.getName(), DbDroidType.BLOB);
        basics.put(Clob.class.getName(), DbDroidType.CLOB);
        basics.put(Serializable.class.getName(), DbDroidType.SERIALIZABLE);

        DATA_TYPES = Collections.unmodifiableMap(basics);
    }

    /**
     * Maps types to their corresponding mapped type
     */
    private Map<String, String> typeToMappedTypeMap;
    /**
     * Maps mapped types to their corresponding type
     */
    private Map<String, String> mappedTypeToTypeMap;

    /**
     * Constructor
     * 
     * @param dataTypeMapped
     */
    public DataType(Map<DbDroidType, String> mappedDataType) {
        if (mappedDataType != null) {
            this.typeToMappedTypeMap = new HashMap<String, String>();
            this.mappedTypeToTypeMap = new HashMap<String, String>();

            for (Map.Entry<String, DbDroidType> entry : DATA_TYPES.entrySet()) {
                String mappedType = mappedDataType.get(entry.getValue());
                if (mappedType != null) {
                    this.typeToMappedTypeMap.put(entry.getKey(), mappedType);
                    this.mappedTypeToTypeMap.put(mappedType, entry.getKey());
                }
            }
        }
    }

    public DbDroidType getDbDroidType(Class<?> clazz) {
        return DATA_TYPES.get(clazz.getName());
    }

    public String getMappedType(Class<?> clazz) {
        return this.typeToMappedTypeMap.get(clazz.getName());
    }

    public String getType(String mappedType) {
        return this.mappedTypeToTypeMap.get(mappedType);
    }
}
