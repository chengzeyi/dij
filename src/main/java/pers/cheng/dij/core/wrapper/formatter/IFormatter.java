package pers.cheng.dij.core.wrapper.formatter;

import com.sun.jdi.Type;

public interface IFormatter {
    String toString(Object value);

    boolean acceptType(Type type);
}
