package pers.cheng.dij.core.wrapper.formatter;

import com.sun.jdi.Type;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;

public class BooleanFormatter implements IValueFormatter {
    @Override
    public String toString(Object value) {
        return value == null ? NullObjectFormatter.NULL_STRING : value.toString();
    }

    @Override
    public boolean acceptType(Type type) {
        if (type == null) {
            return false;
        }
        char signature0 = type.signature().charAt(0);
        return signature0 == TypeIdentifier.BOOLEAN;
    }

    @Override
    public Value valueOf(String value, Type type) {
        VirtualMachine vm = type.virtualMachine();
        return vm.mirrorOf(Boolean.parseBoolean(value));
    }
}
