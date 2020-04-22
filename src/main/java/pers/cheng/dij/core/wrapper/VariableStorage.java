package pers.cheng.dij.core.wrapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;

import com.sun.jdi.BooleanValue;
import com.sun.jdi.ByteValue;
import com.sun.jdi.CharValue;
import com.sun.jdi.DoubleValue;
import com.sun.jdi.FloatValue;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.LongValue;
import com.sun.jdi.ShortValue;
import com.sun.jdi.StringReference;
import com.sun.jdi.Value;

import pers.cheng.dij.Configuration;
import pers.cheng.dij.core.DebugException;
import pers.cheng.dij.core.wrapper.formatter.TypeIdentifier;

public abstract class VariableStorage {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);

    private static Function<Object, List<Object>> DEFAULT_GUESS_FUNCTION = null;
    private static Map<String, Function<Object, List<Object>>> CLASS_NAME_2_GUESS_FUNCTION = new HashMap<>();

    private Class<?> variableClass = null;
    private String variableName = null;
    private Object initialValue = null;
    private List<Object> guessedValues = null;

    static {
        DEFAULT_GUESS_FUNCTION = GuessFunctionProvider::guessAnything;

        CLASS_NAME_2_GUESS_FUNCTION.put(Byte.TYPE.getName(), GuessFunctionProvider::guessByte);
        CLASS_NAME_2_GUESS_FUNCTION.put(Character.TYPE.getName(), GuessFunctionProvider::guessChar);
        CLASS_NAME_2_GUESS_FUNCTION.put(Float.TYPE.getName(), GuessFunctionProvider::guessFloat);
        CLASS_NAME_2_GUESS_FUNCTION.put(Double.TYPE.getName(), GuessFunctionProvider::guessDouble);
        CLASS_NAME_2_GUESS_FUNCTION.put(Integer.TYPE.getName(), GuessFunctionProvider::guessInt);
        CLASS_NAME_2_GUESS_FUNCTION.put(Long.TYPE.getName(), GuessFunctionProvider::guessLong);
        CLASS_NAME_2_GUESS_FUNCTION.put(Short.TYPE.getName(), GuessFunctionProvider::guessShort);
        CLASS_NAME_2_GUESS_FUNCTION.put(Boolean.TYPE.getName(), GuessFunctionProvider::guessBoolean);
        CLASS_NAME_2_GUESS_FUNCTION.put(String.class.getName(), GuessFunctionProvider::guessString);
        CLASS_NAME_2_GUESS_FUNCTION.put(Object.class.getName(), GuessFunctionProvider::guessObject);
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

    protected final void init(String variableName, String variableTypeSignature, Value variableValue) throws DebugException {
        LOGGER.info(String.format("Handling local variable, name: %s, typeSignature: %s, value: %s",
                variableName, variableTypeSignature, variableValue));

        char signature0 = variableTypeSignature.charAt(0);
        switch (signature0) {
            case TypeIdentifier.BYTE:
                variableClass = Byte.TYPE;
                initialValue = ((ByteValue) variableValue).value();
                break;
            case TypeIdentifier.CHAR:
                variableClass = Character.TYPE;
                initialValue = ((CharValue) variableValue).value();
                break;
            case TypeIdentifier.FLOAT:
                variableClass = Float.TYPE;
                initialValue = ((FloatValue) variableValue).value();
                break;
            case TypeIdentifier.DOUBLE:
                variableClass = Double.TYPE;
                initialValue = ((DoubleValue) variableValue).value();
                break;
            case TypeIdentifier.INT:
                variableClass = Integer.TYPE;
                initialValue = ((IntegerValue) variableValue).value();
                break;
            case TypeIdentifier.LONG:
                variableClass = Long.TYPE;
                initialValue = ((LongValue) variableValue).value();
                break;
            case TypeIdentifier.SHORT:
                variableClass = Short.TYPE;
                initialValue = ((ShortValue) variableValue).value();
                break;
            case TypeIdentifier.BOOLEAN:
                variableClass = Boolean.TYPE;
                initialValue = ((BooleanValue) variableValue).value();
                break;
            case TypeIdentifier.STRING:
                variableClass = String.class;
                initialValue = ((StringReference) variableValue).value();
                break;
            default:
                if (variableTypeSignature.equals(TypeIdentifier.STRING_SIGNATURE)) {
                    variableClass = String.class;
                    initialValue = ((StringReference) variableValue).value();
                } else if (signature0 == TypeIdentifier.OBJECT) {
                    variableClass = Object.class;
                    // It is complex to get the initial value of an 'Object' local variable.
                    // Maybe some serialization techniques would work.
                    // Current set it to "unknown" for display purpose.
                    initialValue = "unknown";
                } else {
                    LOGGER.info(String.format("Cannot handle variable type: %s", variableTypeSignature));
                    throw new DebugException(String.format("Cannot handle variable type: %s", variableTypeSignature));
                }
        }

        LOGGER.info(String.format("variableName: %s, variableClass: %s, initialValue: %s", variableName, variableClass, initialValue));

        Function<Object, List<Object>> guessFunction = getGuessFunction(variableClass.getName());
        LOGGER.info(String.format("Got guessFunction: %s", guessFunction));

        guessedValues = guessFunction.apply(initialValue);

        LOGGER.info(String.format("Guessed values are: %s", guessedValues));
    }

    private Function<Object, List<Object>> getGuessFunction(String className) {
        Function<Object, List<Object>> guessFunction = CLASS_NAME_2_GUESS_FUNCTION.get(className);
        return guessFunction == null ? DEFAULT_GUESS_FUNCTION : guessFunction;
    }
}
