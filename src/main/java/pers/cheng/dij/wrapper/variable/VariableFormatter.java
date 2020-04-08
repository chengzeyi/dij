package pers.cheng.dij.wrapper.variable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.sun.jdi.Type;
import com.sun.jdi.Value;

import pers.cheng.dij.wrapper.formatter.*;

public class VariableFormatter {

    private List<IValueFormatter> VALUE_FORMATTER_LIST;
    private List<ITypeFormatter> TYPE_FORMATTER_LIST;

    public VariableFormatter() {
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
        List<? extends IFormatter> validFormatterList =
                formatterList.stream().filter(t -> t.acceptType(type)).collect(Collectors.toList());
        if (validFormatterList.isEmpty()) {
            throw new UnsupportedOperationException(String.format("There is no valid formatter for type %s.",
                    type == null ? "null" : type.name()));
        }
        return formatterList.get(0);
    }

    public String typeToString(Type type) {
        IFormatter formatter = getFormatter(TYPE_FORMATTER_LIST, type);
        return formatter.toString(type);
    }

    public String valueToString(Value value) {
        Type type = value == null? null : value.type();
        IFormatter formatter = getFormatter(VALUE_FORMATTER_LIST, type);
        return formatter.toString(type);
    }

    public Value stringToValue(String string, Type type) {
        IValueFormatter formatter = (IValueFormatter) getFormatter(VALUE_FORMATTER_LIST, type);
        return formatter.valueOf(string, type);
    }
}
