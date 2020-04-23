package pers.cheng.dij.core.wrapper.formatter;

import com.sun.jdi.StringReference;
import com.sun.jdi.Type;
import com.sun.jdi.Value;

import org.apache.commons.lang3.StringUtils;

public class StringObjectFormatter extends ObjectFormatter {
    private static final String QUOTE_STRING = "\"";

    public StringObjectFormatter() {
        super(null);
    }

    @Override
    public String toString(Object value) {
        return String.format("\"%s\"", ((StringReference) value).value());
    }

    @Override
    public boolean acceptType(Type type) {
        return type != null
            && (type.signature().charAt(0) == TypeIdentifier.STRING
                    || type.signature().equals(TypeIdentifier.STRING_SIGNATURE));
    }

    @Override
    public Value valueOf(String value, Type type) {
        if (value == null || NullObjectFormatter.NULL_STRING.equals(value)) {
            return null;
        }
        // TODO: A potential bug cause.
        if (value.length() >= 2
                && value.startsWith(QUOTE_STRING)
                && value.endsWith(QUOTE_STRING)
                && !value.endsWith("\\" + QUOTE_STRING)) {
            return type.virtualMachine().mirrorOf(StringUtils.substring(value, 1, -1));
        }
        return type.virtualMachine().mirrorOf(value);
    }
}
