package pers.cheng.dij.core.wrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.PrimitiveValue;
import com.sun.jdi.StringReference;
import com.sun.jdi.Value;

import pers.cheng.dij.Configuration;

public class GuessFunctionProvider{
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);

    public static List<Object> guessByte(Value value) {
        List<Object> ret = new ArrayList<>(256);
        if (!(value instanceof PrimitiveValue)) {
            LOGGER.severe(String.format("Unsupported value: %s", value));
            return ret;
        }
        // We have 256 possibilities for a byte value.
        for (int i = 0; i < 256; ++i) {
            ret.add((byte) i);
        }
        return ret;
    }

    public static List<Object> guessChar(Value value) {
        List<Object> ret = new ArrayList<>(256);
        if (!(value instanceof PrimitiveValue)) {
            LOGGER.severe(String.format("Unsupported value: %s", value));
            return ret;
        }
        // We have 256 possibilities for a char value.
        for (int i = 0; i < 256; ++i) {
            ret.add((char) i);
        }
        return ret;
    }

    public static List<Object> guessFloat(Value value) {
        List<Object> ret = new ArrayList<>();
        if (!(value instanceof PrimitiveValue)) {
            LOGGER.severe(String.format("Unsupported value: %s", value));
            return ret;
        }
        ret.add(0.0F);
        ret.add(1.0F);
        ret.add(0.5F);
        ret.add(-1.0F);
        ret.add(-0.5F);
        ret.add(Float.MAX_VALUE);
        ret.add(Float.MIN_VALUE);
        ret.add(Float.MIN_NORMAL);
        ret.add(-Float.MAX_VALUE);
        ret.add(-Float.MIN_VALUE);
        ret.add(-Float.MIN_NORMAL);
        ret.add(Float.NaN);
        float floatValue = ((PrimitiveValue) value).floatValue();
        if (!ret.contains(floatValue)) {
            ret.add(floatValue);
        }
        return ret;
    }

    public static List<Object> guessDouble(Value value) {
        List<Object> ret = new ArrayList<>();
        if (!(value instanceof PrimitiveValue)) {
            LOGGER.severe(String.format("Unsupported value: %s", value));
            return ret;
        }
        ret.add(0.0D);
        ret.add(1.0D);
        ret.add(0.5D);
        ret.add(-1.0D);
        ret.add(-0.5D);
        ret.add(Double.MAX_VALUE);
        ret.add(Double.MIN_VALUE);
        ret.add(Double.MIN_NORMAL);
        ret.add(-Double.MAX_VALUE);
        ret.add(-Double.MIN_VALUE);
        ret.add(-Double.MIN_NORMAL);
        ret.add(Double.NaN);
        double doubleValue = ((PrimitiveValue) value).doubleValue();
        if (!ret.contains(doubleValue)) {
            ret.add(doubleValue);
        }
        return ret;
    }

    public static List<Object> guessInt(Value value) {
        List<Object> ret = new ArrayList<>();
        if (!(value instanceof PrimitiveValue)) {
            LOGGER.severe(String.format("Unsupported value: %s", value));
            return ret;
        }
        ret.add(0);
        for (int i = 1; i < 16; ++i) {
            ret.add(i);
            ret.add(-i);
        }

        ret.add(Integer.MAX_VALUE);
        ret.add(Integer.MIN_VALUE);
        int intValue = ((PrimitiveValue) value).intValue();
        if (!ret.contains(intValue)) {
            ret.add(intValue);
        }
        return ret;
    }

    public static List<Object> guessLong(Value value) {
        List<Object> ret = new ArrayList<>();
        if (!(value instanceof PrimitiveValue)) {
            LOGGER.severe(String.format("Unsupported value: %s", value));
            return ret;
        }
        ret.add(0L);
        for (long i = 1; i < 16; ++i) {
            ret.add(i);
            ret.add(-i);
        }
        ret.add(Long.MAX_VALUE);
        ret.add(Integer.MIN_VALUE);
        long longValue = ((PrimitiveValue) value).longValue();
        if (!ret.contains(longValue)) {
            ret.add(longValue);
        }
        return ret;
    }

    public static List<Object> guessShort(Value value) {
        List<Object> ret = new ArrayList<>();
        if (!(value instanceof PrimitiveValue)) {
            LOGGER.severe(String.format("Unsupported value: %s", value));
            return ret;
        }
        ret.add((short) 0);
        for (short i = 1; i < 16; ++i) {
            ret.add(i);
            ret.add(-i);
        }
        ret.add(Short.MAX_VALUE);
        ret.add(Short.MIN_VALUE);
        short shortValue = ((PrimitiveValue) value).shortValue();
        if (!ret.contains(shortValue)) {
            ret.add(shortValue);
        }
        return ret;
    }

    public static List<Object> guessBoolean(Value value) {
        List<Object> ret = new ArrayList<>(2);
        if (!(value instanceof PrimitiveValue)) {
            LOGGER.severe(String.format("Unsupported value: %s", value));
            return ret;
        }
        ret.add(false);
        ret.add(true);
        return ret;
    }

    public static List<Object> guessAnything(Value value) {
        return new ArrayList<>();
    }

    // Not really used yet, since I have not figure out how to set a non-null value for any Object.
    // It is still here for some unification consideration.
    public static List<Object> guessObject(Value value) {
        return Arrays.asList((Object) null);
    }

    public static List<Object> guessStringObject(Value value) {
        if (!(value instanceof StringReference)) {
            LOGGER.severe(String.format("Unsupported value: %s", value));
            return new ArrayList<>();
        }
        String str = ((StringReference) value).value();
        List<Object> ret = Arrays.asList((String) null, "", str + str);
        if (str.length() < 16) {
            for (int i = 1; i < str.length(); ++i) {
                ret.add(str.substring(0, i));
            }
        }
        return ret;
    }

    public static List<Object> guessArrayObject(Value value) {
        if (!(value instanceof ArrayReference)) {
            LOGGER.severe(String.format("Unsupported value: %s", value));
            return new ArrayList<>();
        }
        return Arrays.asList((Object) null);
    }
}
