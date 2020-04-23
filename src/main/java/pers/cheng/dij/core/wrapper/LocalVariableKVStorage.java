package pers.cheng.dij.core.wrapper;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.StackFrame;
import pers.cheng.dij.Configuration;
import pers.cheng.dij.core.DebugException;

public class LocalVariableKVStorage extends VariableKVStorage {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);

    public LocalVariableKVStorage(StackFrame stackFrame) throws DebugException {
        variableName2Storage = new HashMap<>();
        List<LocalVariable> localVariables;
        try {
            localVariables = stackFrame.visibleVariables();
        } catch (AbsentInformationException e) {
            LOGGER.severe(String.format("Cannot get local variables from the stack frame, %s", e));
            throw new DebugException("Cannot get local variables from the stack frame", e);
        }

        for (LocalVariable localVariable : localVariables) {
            String variableName = localVariable.name();
            LocalVariableStorage localVariableStorage;
            try {
                localVariableStorage = new LocalVariableStorage(localVariable, stackFrame);
            } catch (DebugException e) {
                LOGGER.info(String.format("Localvariable %s is not compatible for analysis", localVariable));
                continue;
            }
            variableName2Storage.put(variableName, localVariableStorage);
            guessedTotal += localVariableStorage.getGuessedTotal();
            LOGGER.info(String.format("New local variable added, name: %s, type: %s", localVariable.name(), localVariable.typeName()));
        }

        LOGGER.info(String.format("Got guessed values, total count: %d", guessedTotal));
    }
}
