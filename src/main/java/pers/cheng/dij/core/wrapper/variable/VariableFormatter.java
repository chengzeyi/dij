package pers.cheng.dij.core.wrapper.variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.sun.jdi.Type;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;

import pers.cheng.dij.Configuration;
import pers.cheng.dij.core.DebugException;
import pers.cheng.dij.core.wrapper.formatter.*;

public class VariableFormatter {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);

    private static List<IValueFormatter> VALUE_FORMATTER_LIST;
    private static List<ITypeFormatter> TYPE_FORMATTER_LIST;

    static {
        TYPE_FORMATTER_LIST = new ArrayList<>();
        TYPE_FORMATTER_LIST.add(new SimpleTypeFormatter());

        VALUE_FORMATTER_LIST = new ArrayList<>();
        VALUE_FORMATTER_LIST.add(new StringObjectFormatter());
        VALUE_FORMATTER_LIST.add(new ArrayObjectFormatter(new SimpleTypeFormatter()::toString));
        VALUE_FORMATTER_LIST.add(new ClassObjectFormatter(new SimpleTypeFormatter()::toString));
        VALUE_FORMATTER_LIST.add(new ObjectFormatter(new SimpleTypeFormatter()::toString));
        VALUE_FORMATTER_LIST.add(new NullObjectFormatter());
        VALUE_FORMATTER_LIST.add(new NumericFormatter());
        VALUE_FORMATTER_LIST.add(new CharFormatter());
        VALUE_FORMATTER_LIST.add(new BooleanFormatter());
    }

    private static IFormatter getFormatter(List<? extends IFormatter> formatterList, Type type) {
        LOGGER.info(String.format("Getting formatter for type %s, signature %s", type, type.signature()));
        List<? extends IFormatter> validFormatterList =
                formatterList.stream().filter(t -> t.acceptType(type)).collect(Collectors.toList());
        if (validFormatterList.isEmpty()) {
            throw new UnsupportedOperationException(String.format("There is no valid formatter for type %s.",
                    type == null ? "null" : type.name()));
        }
        LOGGER.info(String.format("Got formatters %s for type %s", validFormatterList, type));
        return validFormatterList.get(0);
    }

    public static String typeToString(Type type) throws DebugException {
        try {
            IFormatter formatter = getFormatter(TYPE_FORMATTER_LIST, type);
            return formatter.toString(type);
        } catch (UnsupportedOperationException e) {
            throw new DebugException(e);
        } 
    }

    public static String valueToString(Value value) throws DebugException {
        Type type = value == null? null : value.type();
        try {
            IFormatter formatter = getFormatter(VALUE_FORMATTER_LIST, type);
            return formatter.toString(value);
        } catch (UnsupportedOperationException e) {
            throw new DebugException(e);
        }
    }

    public static Value stringToValue(String string, Type type) throws DebugException {
        try {
            IValueFormatter formatter = (IValueFormatter) getFormatter(VALUE_FORMATTER_LIST, type);
            return formatter.valueOf(string, type);
        } catch (UnsupportedOperationException e) {
            throw new DebugException(e);
        }
    }

    public static Variable createVariable(String name, Type type, Value value) throws DebugException {
        LOGGER.info(String.format("Handling variable, name: %s, type: %s, value: %s",
                name, type, value));

        String strType;
        String strValue;
        try {
            strType = typeToString(type);
            strValue = valueToString(value);
        } catch (UnsupportedOperationException e) {
            LOGGER.warning(String.format("Cannot handle variable, name: %s, type: %s, value: %s, %s",
                        name, type, value, e));
            throw new DebugException(e);
        }

        LOGGER.info(String.format("Parse variable successfully, name: %s, strType: %s, strValue: %s", name, strType, strValue));
        return new Variable(name, strType, strValue);
    }

    public static List<Variable> createVariables(String name, Type type, List<Object> values) throws DebugException {
        String strType;
        try {
            strType = typeToString(type);
        } catch (UnsupportedOperationException e) {
            LOGGER.severe(String.format("Unable to parse type: %s", type));
            throw new DebugException(e);
        }
        return values.stream().map(value -> {
            String strValue = Objects.toString(value);
            return new Variable(name, strType, strValue);
        }).collect(Collectors.toList());
    }

    public static Value createVariableMirror(Variable variable, Type type) throws DebugException {
        try {
            return stringToValue(variable.getValue(), type);
        } catch (UnsupportedOperationException e) {
            LOGGER.severe(String.format("Unable to parse String to Value, %s", e));
            throw new DebugException(e);
        }
    }
}
