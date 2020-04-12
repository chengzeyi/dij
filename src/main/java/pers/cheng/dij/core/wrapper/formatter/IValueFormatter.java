package pers.cheng.dij.core.wrapper.formatter;

import pers.cheng.dij.core.wrapper.formatter.IFormatter;

import com.sun.jdi.Type;
import com.sun.jdi.Value;

public interface IValueFormatter extends IFormatter {
    Value valueOf(String value, Type type);
}
