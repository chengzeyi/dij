package pers.cheng.dij.wrapper.object;

import java.util.List;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.VMCannotBeModifiedException;
import com.sun.jdi.Value;

import pers.cheng.dij.core.DebugException;

public class ArrayWrapper extends ObjectWrapper {
    public ArrayWrapper(ArrayReference objReference) {
        super(objReference);
    }

    public Value getValue(int i) {
        return ((ArrayReference) objReference).getValue(i);
    }

    public List<Value> getValues() {
        return ((ArrayReference) objReference).getValues();
    }

    public List<Value> getValues(int index, int length) {
        return ((ArrayReference) objReference).getValues(index, length);
    }

    public int getLength() {
        return ((ArrayReference) objReference).length();
    }

    public void setValue(int index, Value value) throws DebugException {
        try {
            ((ArrayReference) objReference).setValue(index, value);
        } catch (InvalidTypeException e) {
            throw new DebugException(
                    String.format("The type %s of value is not campatible with the declared type of the array type %s",
                            value.type().name(), objReference.referenceType().name()));
        } catch (ClassNotLoadedException e) {
            throw new DebugException(String.format("The array component type of array type %s has not been loaded.",
                    objReference.referenceType().name()), e);
        } catch (VMCannotBeModifiedException e) {
            handleVMCannotBeModifiedException();
        }
    }

    public void setValues(List<? extends Value> values) throws DebugException {
        try {
            ((ArrayReference) objReference).setValues(values);
        } catch (InvalidTypeException e) {
            throw new DebugException(String.format(
                    "The type of value is not campatible with the declared type of the array type %s",
                    ((ArrayReference) objReference).referenceType().name()), e);
        } catch (ClassNotLoadedException e) {
            throw new DebugException(String.format(
                    "The array component type of array type %s has not been loaded.",
                    ((ArrayReference) objReference).referenceType().name()), e);
        } catch (VMCannotBeModifiedException e) {
            handleVMCannotBeModifiedException();
        }
    }

    public void setValues(int index, List<? extends Value> values, int srcIndex, int length) throws DebugException {
        try {
            ((ArrayReference) objReference).setValues(index, values, srcIndex, length);
        } catch (InvalidTypeException e) {
            throw new DebugException(String.format(
                    "The type of value is not campatible with the declared type of the array type %s",
                    ((ArrayReference) objReference).referenceType().name()), e);
        } catch (ClassNotLoadedException e) {
            throw new DebugException(String.format(
                    "The array component type of array type %s has not been loaded.",
                    ((ArrayReference) objReference).referenceType().name()), e);
        } catch (VMCannotBeModifiedException e) {
            handleVMCannotBeModifiedException();
        }
    }
}
