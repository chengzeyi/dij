package pers.cheng.dij.core.wrapper;

import java.util.ArrayList;
import java.util.List;

public class GuessFunctionProvider{
    public static List<Object> guessByte(Object object) {
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

    public static List<Object> guessChar(Object object) {
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

    public static List<Object> guessFloat(Object object) {
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

    public static List<Object> guessDouble(Object object) {
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

    public static List<Object> guessInt(Object object) {
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

    public static List<Object> guessLong(Object object) {
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

    public static List<Object> guessShort(Object object) {
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

    public static List<Object> guessBoolean(Object object) {
        List<Object> ret = new ArrayList<>(2);
        if (!(object instanceof Boolean)) {
            return ret;
        }
        ret.add(false);
        ret.add(true);
        return ret;
    }

    public static List<Object> guessAnyth(Object object) {
        return new ArrayList<>();
    }
}
