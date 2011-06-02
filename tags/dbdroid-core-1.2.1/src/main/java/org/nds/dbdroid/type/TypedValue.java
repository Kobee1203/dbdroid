package org.nds.dbdroid.type;

public class TypedValue {

    private final Object value;
    private final DbDroidType type;

    public TypedValue(Object value, DbDroidType type) {
        this.value = value;
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public DbDroidType getType() {
        return type;
    }

    @Override
    public String toString() {
        return value == null ? "null" : value.toString();
    }
}
