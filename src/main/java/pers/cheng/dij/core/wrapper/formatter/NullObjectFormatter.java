package pers.cheng.dij.core.wrapper.formatter;

import com.sun.jdi.Type;
import com.sun.jdi.Value;

public class NullObjectFormatter implements IValueFormatter {
    public static final String NULL_STRING = "null";

    @Override
    public String toString(Object value) {
        return NULL_STRING;
    }

    @Override
    public boolean acceptType(Type type) {
        return type == null;
    }

    @Override
    public Value valueOf(String value, Type type) {
        if (value == null || NULL_STRING.equals(value)) {
            return null;
        }
        throw new UnsupportedOperationException("The string value is not supported by NullObjectFormatter.");
    }
}
