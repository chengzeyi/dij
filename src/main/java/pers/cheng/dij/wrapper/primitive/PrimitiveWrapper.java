package pers.cheng.dij.wrapper.primitive;

import com.sun.jdi.PrimitiveValue;
import com.sun.jdi.Type;
import pers.cheng.dij.wrapper.IValueWrapper;

public class PrimitiveWrapper implements IValueWrapper {
    protected PrimitiveValue primitiveValue;

    PrimitiveWrapper(PrimitiveValue primitiveValue) {
        this.primitiveValue = primitiveValue;
    }

    public Type getType() {
        return primitiveValue.type();
    }

    public boolean getBooleanValue() {
        return primitiveValue.booleanValue();
    }

    public byte getByteValue() {
        return primitiveValue.byteValue();
    }

    public char getCharValue() {
        return primitiveValue.charValue();
    }

    public double getDoubleValue() {
        return primitiveValue.doubleValue();
    }

    public float getFloatValue() {
        return primitiveValue.floatValue();
    }

    public int getIntValue() {
        return primitiveValue.intValue();
    }

    public long getLongValue() {
        return primitiveValue.longValue();
    }

    public short getShortValue() {
        return primitiveValue.shortValue();
    }
}
