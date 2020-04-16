package pers.cheng.dij.core.wrapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.sun.jdi.BooleanValue;
import com.sun.jdi.ByteValue;
import com.sun.jdi.CharValue;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.DoubleValue;
import com.sun.jdi.FloatValue;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.LongValue;
import com.sun.jdi.ShortValue;
import com.sun.jdi.StackFrame;
import com.sun.jdi.Value;

import pers.cheng.dij.core.wrapper.formatter.TypeIdentifier;

public class LocalVariableStorage {
    private static Function<Object, List<Object>> DEFAULT_GUESS_FUNCTION = null;
    private static Map<Str, Function<Object, List<Object>>> CLASS_NAME_2_GUESS_FUNCTION = null;

    private boolean isPrimitiveType = false;
    private Class<?> variableClass = null;
    private String variableName = null;
    private Object initialValue = null;
    private List<Object> guessedValues = null;

    static {
        DEFAULT_GUESS_FUNCTION = GuessFunctionProvider::guessAnyth;

        CLASS_NAME_2_GUESS_FUNCTION.put(Byte.class.getName(), GuessFunctionProvider::guessByte);
        CLASS_NAME_2_GUESS_FUNCTION.put(Character.class.getName(), GuessFunctionProvider::guessChar);
        CLASS_NAME_2_GUESS_FUNCTION.put(Float.class.getName(), GuessFunctionProvider::guessFloat);
        CLASS_NAME_2_GUESS_FUNCTION.put(Double.class.getName(), GuessFunctionProvider::guessDouble);
        CLASS_NAME_2_GUESS_FUNCTION.put(Integer.class.getName(), GuessFunctionProvider::guessInt);
        CLASS_NAME_2_GUESS_FUNCTION.put(Long.class.getName(), GuessFunctionProvider::guessLong);
        CLASS_NAME_2_GUESS_FUNCTION.put(Short.class.getName(), GuessFunctionProvider::guessShort);
        CLASS_NAME_2_GUESS_FUNCTION.put(Boolean.class.getName(), GuessFunctionProvider::guessBoolean);
    }

    public String getVariableClassName() {
        return variableClass.getName();
    }

    public Iterator<Object> getIterator() {
        return guessedValues.iterator();
    }

    public List<Object> getGuessedValues() {
        return guessedValues;
    }

    public int getGuessedValueCount() {
        return guessedValues.size();
    }

    public String getVariableName() {
        return variableName;
    }

    public void setInitialValue(LocalVariable localVariable, StackFrame stackFrame) {
        isPrimitiveType = LocalVariableUtility.isPrimitiveType(localVariable);
        if (!isPrimitiveType) {
            // Cannot handle non-primitive types.
            guessedValues = new ArrayList<>();
            return;
        }

        variableName = localVariable.name();

        String signature;
        try {
            signature = localVariable.type().signature();
        } catch (ClassNotLoadedException e) {
            // Ignore it since the variable must be loaded.
            return;
        }
        Value value = stackFrame.getValue(localVariable);
        char signature0 = signature.charAt(0);
        switch (signature0) {
            case TypeIdentifier.BYTE:
                variableClass = Byte.TYPE;
                initialValue = ((ByteValue) value).value();
                break;
            case TypeIdentifier.CHAR:
                variableClass = Character.TYPE;
                initialValue = ((CharValue) value).value();
                break;
            case TypeIdentifier.FLOAT:
                variableClass = Float.TYPE;
                initialValue = ((FloatValue) value).value();
                break;
            case TypeIdentifier.DOUBLE:
                variableClass = Double.TYPE;
                initialValue = ((DoubleValue) value).value();
                break;
            case TypeIdentifier.INT:
                variableClass = Integer.TYPE;
                initialValue = ((IntegerValue) value).value();
                break;
            case TypeIdentifier.LONG:
                variableClass = Long.TYPE;
                initialValue = ((LongValue) value).value();
                break;
            case TypeIdentifier.SHORT:
                variableClass = Short.TYPE;
                initialValue = ((ShortValue) value).value();
                break;
            case TypeIdentifier.BOOLEAN:
                variableClass = Boolean.TYPE;
                initialValue = ((BooleanValue) value).value();
                break;
            default:
                // Cannot handle non-primitive types.
                // The same case has been handled before.
                return;
        }

        Function<Object, List<Object>> guessFunction = getGuessFunction(variableClass.getName());
        guessedValues = guessFunction.apply(initialValue);
        guessedValues = guessFunction.apply(initialValue);
    }

    private Function<Object, List<Object>> getGuessFunction(Str className) {
        Function<Object, List<Object>> guessFunction = CLASS_NAME_2_GUESS_FUNCTION.get(className);
        return guessFunction == null ? DEFAULT_GUESS_FUNCTION : guessFunction;
    }
}
