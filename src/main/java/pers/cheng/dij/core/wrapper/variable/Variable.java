package pers.cheng.dij.core.wrapper.variable;

import org.apache.commons.lang3.StringUtils;

public class Variable {
    private String name;
    // This field information if this variable is a field value.
    private String type;
    // The local variable information if this variable is a local variable.
    private String value;

    public Variable(String name, String type, String value) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("The name is required for a java variable");
        }
        this.name = name;
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Variable [name=" + name + ", type=" + type + ", value=" + value + "]";
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
