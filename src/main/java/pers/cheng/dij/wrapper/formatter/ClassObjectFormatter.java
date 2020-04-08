package pers.cheng.dij.wrapper.formatter;

import java.util.function.Function;

import com.sun.jdi.ClassObjectReference;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.Type;

public class ClassObjectFormatter extends ObjectFormatter {

    public ClassObjectFormatter(Function<Type, String> typeToStringFunction) {
        super(typeToStringFunction);
    }

    @Override
    public boolean acceptType(Type type) {
        return type != null && type.signature().charAt(0) == TypeIdentifier.CLASS_OBJECT
            || type.signature().equals(TypeIdentifier.CLASS_SIGNATURE);
    }

    @Override
    public String getPrefix(ObjectReference value) {
        Type classType = ((ClassObjectReference) value).reflectedType();
        return String.format("%s (%s)", super.getPrefix(value), typeToStringFunction.apply(classType));
    }
}
