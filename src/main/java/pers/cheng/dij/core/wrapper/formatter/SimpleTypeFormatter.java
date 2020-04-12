package pers.cheng.dij.core.wrapper.formatter;

import com.sun.jdi.Type;

public class SimpleTypeFormatter implements ITypeFormatter {

    @Override
    public String toString(Object type) {
        if (type == null) {
            return NullObjectFormatter.NULL_STRING;
        }

        return ((Type) type).name();
    }

    @Override
    public boolean acceptType(Type type) {
        return true;
    }
}
