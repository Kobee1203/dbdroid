package org.nds.dbdroid.unit;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.ArrayUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nds.dbdroid.query.QueryValueResolver;
import org.nds.logging.Logger;
import org.nds.logging.LoggerFactory;

public class Tests {

    private static final Logger log = LoggerFactory.getLogger(Tests.class);

    private static QueryValueResolver queryValueResolver;

    @BeforeClass
    public static void setUp() {
        queryValueResolver = new QueryValueResolver() {

            @Override
            protected String toStringString(String value) {
                return "String (" + value + ")";
            }

            @Override
            protected String toShortString(Short value) {
                return "Short (" + value + ")";
            }

            @Override
            protected String toNumberString(Number value) {
                return "Number (" + value + ")";
            }

            @Override
            protected String toNullString(Object value) {
                return "Null (" + value + ")";
            }

            @Override
            protected String toLongString(Long value) {
                return "Long (" + value + ")";
            }

            @Override
            protected String toIntegerString(Integer value) {
                return "Integer (" + value + ")";
            }

            @Override
            protected String toFloatString(Float value) {
                return "Float (" + value + ")";
            }

            @Override
            protected String toDoubleString(Double value) {
                return "Double (" + value + ")";
            }

            @Override
            protected String toCollectionString(Collection<?> values) {
                StringBuilder sb = new StringBuilder();
                int i = 0;
                for (Object value : values) {
                    if (i > 0) {
                        sb.append(", ");
                    }
                    sb.append(queryValueResolver.toString(value));
                    i++;
                }

                return "Collection value: " + sb.toString();
            }

            @Override
            protected String toCharacterString(Character value) {
                return "Character (" + value + ")";
            }

            @Override
            protected String toByteString(Byte value) {
                return "Byte (" + value + ")";
            }

            @Override
            protected String toBooleanString(Boolean value) {
                return "Boolean (" + value + ")";
            }

            @Override
            protected String toMapString(Map<?, ?> value) {
                return "Map (" + value + ")";
            }

            @Override
            protected String toObjectString(Object value) {
                return "Object (" + value + ")";
            }
        };
    }

    @Test
    public void test() {

        Object[] values = new Object[] { "string1", true, (short) 1, new BigDecimal(1), null, (long) 1, (int) 1, 1f, (double) 1,
                Arrays.asList("string1", "string2"), 'a', (byte) 0x28, new byte[] { (byte) 0x28 }, new String("string 2"), new Boolean(true),
                new Short((short) 1), new AtomicInteger(1), null, new Long(1), new Integer(1), new Float(1), new Double(1),
                ArrayUtils.toMap(new Object[][] { { 1, "string 1" }, { 2, "string 2" } }), new Character('a'), new Byte((byte) 0x28),
                new Byte[] { new Byte((byte) 0x28) }, new Tests() };

        for (Object obj : values) {
            log.debug(queryValueResolver.toString(obj));
        }
    }

}
