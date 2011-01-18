package org.nds.dbdroid.sqlite;

import java.util.Collection;
import java.util.Map;

import org.nds.dbdroid.query.QueryValueResolver;

public class SQLiteQueryValueResolver extends QueryValueResolver {

    @Override
    protected String toNullString(Object value) {
        return "";
    }

    @Override
    protected String toStringString(String value) {
        return "'" + value.toString() + "'";
    }

    @Override
    protected String toBooleanString(Boolean value) {
        return value.toString();
    }

    @Override
    protected String toByteString(Byte value) {
        return value.toString();
    }

    @Override
    protected String toShortString(Short value) {
        return value.toString();
    }

    @Override
    protected String toCharacterString(Character value) {
        return "'" + value.toString() + "'";
    }

    @Override
    protected String toIntegerString(Integer value) {
        return value.toString();
    }

    @Override
    protected String toFloatString(Float value) {
        return value.toString();
    }

    @Override
    protected String toLongString(Long value) {
        return value.toString();
    }

    @Override
    protected String toDoubleString(Double value) {
        return value.toString();
    }

    @Override
    protected String toNumberString(Number value) {
        return value.toString();
    }

    @Override
    protected String toCollectionString(Collection<?> values) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        int i = 0;
        for (Object value : values) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(toString(value));
            i++;
        }
        sb.append(")");

        return sb.toString();
    }

    @Override
    protected String toMapString(Map<?, ?> value) {
        return null;
    }

    @Override
    protected String toObjectString(Object value) {
        return null;
    }

}
