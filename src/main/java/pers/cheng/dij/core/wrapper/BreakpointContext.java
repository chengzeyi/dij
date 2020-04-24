package pers.cheng.dij.core.wrapper;

import java.util.logging.Logger;

import com.sun.jdi.StackFrame;

import pers.cheng.dij.Configuration;
import pers.cheng.dij.core.DebugException;
import pers.cheng.dij.core.wrapper.storage.LocalVariableKVStorage;
import pers.cheng.dij.core.wrapper.storage.ThisObjectStorage;
import pers.cheng.dij.core.wrapper.variable.Variable;

public class BreakpointContext {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);

    private LocalVariableKVStorage localVariableKVStorage;
    private ThisObjectStorage thisObjectStorage;

    public BreakpointContext(StackFrame topStackFrame) throws DebugException {
        try {
            localVariableKVStorage = new LocalVariableKVStorage(topStackFrame);
        } catch (DebugException e) {
            LOGGER.severe(String.format("Failed to generage local variable KV storage, %s", e));
        }

        try {
            thisObjectStorage = new ThisObjectStorage(topStackFrame);
        } catch (DebugException e) {
            LOGGER.severe(String.format("Failed to generage this object storage, %s", e));
        }

        if (localVariableKVStorage == null && thisObjectStorage == null) {
            LOGGER.severe(String.format("Cannot process local variables and this object of stack frame %s", topStackFrame));
            throw new DebugException(String.format("Cannot process local variables and this object of stack frame %s", topStackFrame));
        }
    }

    public boolean hasNextGuessedLocalVariable() {
        return localVariableKVStorage != null && localVariableKVStorage.hasNext();
    }

    public Variable nextGuessedLocalVariable() {
        return localVariableKVStorage.next();
    }

    public String getGuessedLocalVariableName() {
        return localVariableKVStorage.getCurrentVariableName();
    }

    public Variable getOriginalLocaVariable() {
        return localVariableKVStorage.getOriginalVariable();
    }

    public boolean hasNextGuessedThisObjectField() {
        return thisObjectStorage != null && thisObjectStorage.hasNext();
    }

    public Variable nextGuessedThisObjectField() {
        return thisObjectStorage.next();
    }

    public String getGuessedThisObjectFieldName() {
        return thisObjectStorage.getCurrentVariableName();
    }

    public Variable getOriginalThisObjectField() {
        return thisObjectStorage.getOriginalVariable();
    }
}
