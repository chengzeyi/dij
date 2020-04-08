package pers.cheng.dij.wrapper.formatter;

import java.util.function.Function;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.Type;
import com.sun.jdi.Value;

public class ArrayObjectFormatter extends ObjectFormatter {

    public ArrayObjectFormatter(Function<Type, String> typeToStringFunction) {
        super(typeToStringFunction);
    }
    

    private static int getArrayLength(Value value) {
        return ((ArrayReference) value).length();
    }

    // Resurn the type and length of the array.
    @Override
    public String getPrefix(ObjectReference value) {
        String withLength = String.format("[%s]", NumericFormatter.formatNumber(getArrayLength(value)));
        return super.getPrefix(value).replaceFirst("\\[]", withLength);
    }

    @Override
    public boolean acceptType(Type type) {
        return type != null && type.signature().charAt(0) == TypeIdentifier.ARRAY;
    }
}
