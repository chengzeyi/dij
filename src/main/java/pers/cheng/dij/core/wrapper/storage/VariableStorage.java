package pers.cheng.dij.core.wrapper.storage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import com.sun.jdi.Type;
import com.sun.jdi.Value;

import pers.cheng.dij.Configuration;
import pers.cheng.dij.core.DebugException;
import pers.cheng.dij.core.wrapper.GuessFunctionProvider;
import pers.cheng.dij.core.wrapper.formatter.TypeIdentifier;
import pers.cheng.dij.core.wrapper.variable.Variable;
import pers.cheng.dij.core.wrapper.variable.VariableFormatter;

public abstract class VariableStorage {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);

    private static Function<Value, List<Object>> DEFAULT_GUESS_FUNCTION = null;
    private static Map<Character, Function<Value, List<Object>>> SIGNATURE0_2_GUESS_FUNCTION = new HashMap<>();

    private Variable originalVariable;
    private List<Variable> guessedVariables;

    static {
        DEFAULT_GUESS_FUNCTION = GuessFunctionProvider::guessAnything;

        SIGNATURE0_2_GUESS_FUNCTION.put(TypeIdentifier.BYTE,    GuessFunctionProvider::guessByte);
        SIGNATURE0_2_GUESS_FUNCTION.put(TypeIdentifier.CHAR,    GuessFunctionProvider::guessChar);
        SIGNATURE0_2_GUESS_FUNCTION.put(TypeIdentifier.FLOAT,   GuessFunctionProvider::guessFloat);
        SIGNATURE0_2_GUESS_FUNCTION.put(TypeIdentifier.DOUBLE,  GuessFunctionProvider::guessDouble);
        SIGNATURE0_2_GUESS_FUNCTION.put(TypeIdentifier.INT,     GuessFunctionProvider::guessInt);
        SIGNATURE0_2_GUESS_FUNCTION.put(TypeIdentifier.LONG,    GuessFunctionProvider::guessLong);
        SIGNATURE0_2_GUESS_FUNCTION.put(TypeIdentifier.SHORT,   GuessFunctionProvider::guessShort);
        SIGNATURE0_2_GUESS_FUNCTION.put(TypeIdentifier.BOOLEAN, GuessFunctionProvider::guessBoolean);
        SIGNATURE0_2_GUESS_FUNCTION.put(TypeIdentifier.STRING,  GuessFunctionProvider::guessStringObject);
        SIGNATURE0_2_GUESS_FUNCTION.put(TypeIdentifier.ARRAY,   GuessFunctionProvider::guessArrayObject);
        SIGNATURE0_2_GUESS_FUNCTION.put(TypeIdentifier.OBJECT,  GuessFunctionProvider::guessObject);
    }

    public Iterator<Variable> getIterator() {
        return guessedVariables.iterator();
    }

    public int getGuessedTotal() {
        return guessedVariables.size();
    }

    protected final void init(String name, Type type, Value value) throws DebugException {
        originalVariable = VariableFormatter.createVariable(name, type, value);

        Function<Value, List<Object>> guessFunction = getGuessFunction(type);
        List<Object> guessedResults = guessFunction.apply(value);
        if (guessedResults.isEmpty()) {
            LOGGER.severe(String.format("No guessed results, name: %s, type: %s, value: %s", name, type, value));
            throw new DebugException(String.format("No guessed results, name: %s, type: %s, value: %s", name, type, value));
        }
        guessedVariables = VariableFormatter.createVariables(name, type, guessedResults);

        LOGGER.info(String.format("Guessed variables are %s", guessedVariables));
    }

    private Function<Value, List<Object>> getGuessFunction(Type type) {
        String signature = type.signature();
        // Currently this is the only exception.
        if (signature.equals(TypeIdentifier.STRING_SIGNATURE)) {
            return GuessFunctionProvider::guessStringObject;
        }

        char signature0 = signature.charAt(0);
        Function<Value, List<Object>> guessFunction = SIGNATURE0_2_GUESS_FUNCTION.get(signature0);

        return guessFunction == null ? DEFAULT_GUESS_FUNCTION : guessFunction;
    }

    public Variable getOriginalVariable() {
        return originalVariable;
    }
}
