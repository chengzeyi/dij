package pers.cheng.dij.core.wrapper.formatter;

import com.sun.jdi.Type;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;

public class NumericFormatter implements IValueFormatter {
    private static final String NUMERIC_FORMAT = "%d";

    @Override
    public String toString(Object object) {
        Value value = (Value) object;
        char signature0 = value.type().signature().charAt(0);
        switch (signature0) {
            case TypeIdentifier.LONG:
            case TypeIdentifier.INT:
            case TypeIdentifier.SHORT:
            case TypeIdentifier.BYTE:
                return formatNumber(Long.parseLong(value.toString()));
            default:
                if (hasFraction(signature0)) {
                    return formatFloatDouble(Double.parseDouble(value.toString()));
                }
                throw new UnsupportedOperationException(String.format("%s is not a numeric type.", value.type().name()));
        }
    }

    @Override
    public Value valueOf(String value, Type type) {
        VirtualMachine vm = type.virtualMachine();
        char signature0 = type.signature().charAt(0);

        switch (signature0) {
            case TypeIdentifier.LONG:
            case TypeIdentifier.INT:
            case TypeIdentifier.SHORT:
            case TypeIdentifier.BYTE:
                long number = parseNumber(value);
                switch (signature0) {
                    case TypeIdentifier.LONG:
                        return vm.mirrorOf(number);
                    case TypeIdentifier.INT:
                        return vm.mirrorOf((int) number);
                    case TypeIdentifier.SHORT:
                        return vm.mirrorOf((short) number);
                    default:
                        return vm.mirrorOf((byte) number);
               }
            default:
                if (hasFraction(signature0)) {
                    double doubleNumber = parseFloatDouble(value);
                    if (signature0 == TypeIdentifier.DOUBLE) {
                        return vm.mirrorOf(doubleNumber);
                    } else {
                        return vm.mirrorOf((float) doubleNumber);
                    }
                }
                throw new UnsupportedOperationException(String.format("%s is not a numeric type.", type.name()));
        }
    }

    @Override
    public boolean acceptType(Type type) {
        if (type == null) {
            return false;
        }
        char signature0 = type.signature().charAt(0);
        switch (signature0) {
            case TypeIdentifier.LONG:
            case TypeIdentifier.INT:
            case TypeIdentifier.SHORT:
            case TypeIdentifier.BYTE:
            case TypeIdentifier.FLOAT:
            case TypeIdentifier.DOUBLE:
                return true;
            default:
                return false;
        }
    }

    public static String formatNumber(long value) {
        return String.format(NUMERIC_FORMAT, value);
    }

    private static long parseNumber(String number) {
        return Long.decode(number);
    }

    private static double parseFloatDouble(String number) {
        return Double.parseDouble(number);
    }

    private static String formatFloatDouble(double value) {
        return String.format("%f", value);
    }

    private static boolean hasFraction(char signature0) {
        return signature0 == TypeIdentifier.FLOAT || signature0 == TypeIdentifier.DOUBLE;
    }
}
