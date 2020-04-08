package pers.cheng.dij.wrapper.formatter;

import pers.cheng.dij.wrapper.formatter.IFormatter;

import com.sun.jdi.Type;
import com.sun.jdi.Value;

public interface IValueFormatter extends IFormatter {
    Value valueOf(String value, Type type);
}
