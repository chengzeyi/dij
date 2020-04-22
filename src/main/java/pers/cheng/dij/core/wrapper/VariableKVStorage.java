package pers.cheng.dij.core.wrapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.StackFrame;

import pers.cheng.dij.Configuration;
import pers.cheng.dij.core.DebugException;

public abstract class VariableKVStorage implements Iterator<Object> {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);

    // This class does not keep any instance in JDI.
    // Because the JDI instance might get killed.
    protected Map<String, VariableStorage> variableName2Storage = new HashMap<>();
    protected int currentIdx = 0;
    protected int guessedValueTotal = 0;

    private Iterator<String> variableNameIter;
    private Iterator<Object> guessedValueIter;
    // The class name for an Object, or the class name representing the primitive type.
    private String currentVariableClassName;
    private String currentVariableName;

    @Override
    public boolean hasNext() {
        return currentIdx < guessedValueTotal;
    }

    @Override
    public Object next() {
        if (!hasNext()) {
            LOGGER.severe("No next item left");
            return null;
        }
        ++currentIdx;
        while (true) {
            if (variableNameIter == null) {
                LOGGER.info("The variableNameIter needs initializing");
                variableNameIter = variableName2Storage.keySet().iterator();
            } else if (guessedValueIter.hasNext()) {
                Object guessedValue = guessedValueIter.next();
                LOGGER.info(String.format("Got guessedValue from localVariableStorage, value: %s", guessedValue));
                return guessedValue;
            }
            currentVariableName = variableNameIter.next();
            LOGGER.info(String.format("Current guessed variable name: %s", currentVariableName));
            VariableStorage localVariableStorage = variableName2Storage.get(currentVariableName);
            currentVariableClassName = localVariableStorage.getVariableClassName();
            LOGGER.info(String.format("Current guessed variable className: %s", currentVariableClassName));
            guessedValueIter = localVariableStorage.getIterator();
        }
    }
    
    public String getCurrentVariableClassName() {
        return currentVariableClassName;
    }

    public String getCurrentVariableName() {
        return currentVariableName;
    }
}
