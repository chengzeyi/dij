package pers.cheng.dij.wrapper;

import java.util.ArrayList;
import java.util.List;

public class GuessingFunctionProvider{
    public static List<Object> guessingByte(Object object) {
        List<Object> ret = new ArrayList<>(256);
        if (!(object instanceof Byte)) {
            return ret;
        }
        // We have 256 possibilities for a byte value.
        for (int i = 0; i < 256; ++i) {
            ret.add((byte) i);
        }
        return ret;
    }

    public static List<Object> guessingChar(Object object) {
        List<Object> ret = new ArrayList<>(256);
        if (!(object instanceof Character)) {
            return ret;
        }
        // We have 256 possibilities for a char value.
        for (int i = 0; i < 256; ++i) {
            ret.add((char) i);
        }
        return ret;
    }

    public static List<Object> guessingFloat(Object object) {
        List<Object> ret = new ArrayList<>();
        if (!(object instanceof Float)) {
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
        if (!ret.contains(object)) {
            ret.add(object);
        }
        return ret;
    }

    public static List<Object> guessingDouble(Object object) {
        List<Object> ret = new ArrayList<>();
        if (!(object instanceof Double)) {
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
        if (!ret.contains(object)) {
            ret.add(object);
        }
        return ret;
    }

    public static List<Object> guessingInt(Object object) {
        List<Object> ret = new ArrayList<>();
        if (!(object instanceof Integer)) {
            return ret;
        }
        ret.add(0);
        ret.add(1);
        ret.add(-1);
        ret.add(Integer.MAX_VALUE);
        ret.add(Integer.MIN_VALUE);
        if (!ret.contains(object)) {
            ret.add(object);
        }
        return ret;
    }

    public static List<Object> guessingLong(Object object) {
        List<Object> ret = new ArrayList<>();
        if (!(object instanceof Long)) {
            return ret;
        }
        ret.add(0L);
        ret.add(1L);
        ret.add(-1L);
        ret.add(Long.MAX_VALUE);
        ret.add(Integer.MIN_VALUE);
        if (!ret.contains(object)) {
            ret.add(object);
        }
        return ret;
    }

    public static List<Object> guessingShort(Object object) {
        List<Object> ret = new ArrayList<>();
        if (!(object instanceof Short)) {
            return ret;
        }
        ret.add((short) 0);
        ret.add((short) 1);
        ret.add((short) -1);
        ret.add(Short.MAX_VALUE);
        ret.add(Short.MIN_VALUE);
        if (!ret.contains(object)) {
            ret.add(object);
        }
        return ret;
    }

    public static List<Object> guessingBoolean(Object object) {
        List<Object> ret = new ArrayList<>(2);
        if (!(object instanceof Short)) {
            return ret;
        }
        ret.add(false);
        ret.add(true);
        return ret;
    }

    public static List<Object> guessingAnything(Object object) {
        return new ArrayList<>();
    }
}
