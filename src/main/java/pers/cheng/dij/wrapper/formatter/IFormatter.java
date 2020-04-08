package pers.cheng.dij.wrapper.formatter;

import com.sun.jdi.Type;

public interface IFormatter {
    String toString(Object value);

    boolean acceptType(Type type);
}
