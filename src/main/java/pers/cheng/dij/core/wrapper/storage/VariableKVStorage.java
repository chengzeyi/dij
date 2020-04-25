package pers.cheng.dij.core.wrapper.storage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import pers.cheng.dij.Configuration;
import pers.cheng.dij.core.wrapper.variable.Variable;

public abstract class VariableKVStorage implements Iterator<Object> {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);

    // This class does not keep any instance in JDI.
    // Because the JDI instance might get killed.
    protected Map<String, VariableStorage> variableName2Storage = new HashMap<>();
    protected int currentIdx = 0;
    protected int guessedTotal = 0;

    private Iterator<String> variableNameIter;
    private Iterator<Variable> guessedVariableIter;
    // The class name for an Object, or the class name representing the primitive type.
    private String currentVariableName;
    private Variable originalVariable;

    @Override
    public boolean hasNext() {
        return currentIdx < guessedTotal;
    }

    @Override
    public Variable next() {
        if (!hasNext()) {
            LOGGER.severe("No next item left");
            return null;
        }
        ++currentIdx;
        while (true) {
            if (variableNameIter == null) {
                LOGGER.info("The variableNameIter needs initializing");
                variableNameIter = variableName2Storage.keySet().iterator();
            } else if (guessedVariableIter.hasNext()) {
                Variable guessedVariable = guessedVariableIter.next();
                LOGGER.info(String.format("Got guessed variable %s from variableStorage", guessedVariable));
                return guessedVariable;
            }
            currentVariableName = variableNameIter.next();
            LOGGER.info(String.format("Current guessed variable name: %s", currentVariableName));
            VariableStorage variableStorage = variableName2Storage.get(currentVariableName);
            originalVariable = variableStorage.getOriginalVariable();
            LOGGER.info(String.format("Current guessed variable's original form: %s", originalVariable));
            guessedVariableIter = variableStorage.getIterator();
        }
    }
    
    public String getCurrentVariableName() {
        return currentVariableName;
    }

    public Variable getOriginalVariable() {
        return originalVariable;
    }
    
    protected final VariableStorage put(VariableStorage variableStorage) {
        String key = variableStorage.getOriginalVariable().getName();
        guessedTotal += variableStorage.getGuessedTotal();
        LOGGER.info(String.format("New variable storage added, name: %s, type: %s", key, variableStorage.getOriginalVariable().getType()));
        return variableName2Storage.put(key, variableStorage);
    }
}
