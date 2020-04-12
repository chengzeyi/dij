package pers.cheng.dij.core.wrapper.variable;

import com.sun.jdi.Field;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Type;
import com.sun.jdi.Value;

import org.apache.commons.lang3.StringUtils;

public class Variable {
    protected Value value;
    // The name of this variable.
    protected String name;
    // This field information if this variable is a field value.
    protected Field field;
    // The local variable information if this variable is a local variable.
    protected LocalVariable localVariable;
    // The argument index if this variable is an argument variable.
    // If this variable is not an argument variable, it is -1.
    protected int argumentIndex;

    public Variable(String name, Value value) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("The name is required for a java variable");
        }
        this.name = name;
        this.value = value;
        this.argumentIndex = -1;
    }

    public Type getDeclaringType() {
        if (this.field != null) {
            return this.field.declaringType();
        }
        return null;
    }

    public Value getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public Field getField() {
        return field;
    }

    public LocalVariable getLocalVariable() {
        return localVariable;
    }

    public int getArgumentIndex() {
        return argumentIndex;
    }
}
