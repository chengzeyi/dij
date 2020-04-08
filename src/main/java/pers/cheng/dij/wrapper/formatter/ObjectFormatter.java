package pers.cheng.dij.wrapper.formatter;

import java.util.function.Function;

import com.sun.jdi.ObjectReference;
import com.sun.jdi.Type;
import com.sun.jdi.Value;

public class ObjectFormatter implements IValueFormatter {
    protected final Function<Type, String> typeToStringFunction;

    public ObjectFormatter(Function<Type, String> typeToStringFunction) {
        this.typeToStringFunction = typeToStringFunction;
    }

    @Override
    public String toString(Object object) {
        return String.format("%s@%s", getPrefix((ObjectReference) object), getIdPostfix((ObjectReference) object));
    }

    @Override
    public boolean acceptType(Type type) {
        if (type == null) {
            return false;
        }
        char tag = type.signature().charAt(0);
        switch (tag) {
            case TypeIdentifier.OBJECT:
            case TypeIdentifier.ARRAY:
            case TypeIdentifier.STRING:
            case TypeIdentifier.THREAD:
            case TypeIdentifier.THREAD_GROUP:
            case TypeIdentifier.CLASS_LOADER:
            case TypeIdentifier.CLASS_OBJECT:
                return true;
            default:
                return false;
        }
    }

    @Override
    public Value valueOf(String value, Type type) {
        if (value == null || NullObjectFormatter.NULL_STRING.equals(value)) {
            return null;
        }
        throw new UnsupportedOperationException(
                String.format("The string value is not supported yet for type %s.", type.name()));
    }

    public String getPrefix(ObjectReference value) {
        return typeToStringFunction.apply(value.type());
    }

    public String getIdPostfix(ObjectReference object) {
        return NumericFormatter.formatNumber(object.uniqueID());
    }
}
