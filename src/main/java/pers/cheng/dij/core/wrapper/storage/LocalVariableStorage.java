package pers.cheng.dij.core.wrapper.storage;

import java.util.logging.Logger;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.StackFrame;
import com.sun.jdi.Type;
import com.sun.jdi.Value;

import pers.cheng.dij.Configuration;
import pers.cheng.dij.core.DebugException;
import pers.cheng.dij.core.wrapper.variable.VariableType;

public class LocalVariableStorage extends VariableStorage {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);

    public LocalVariableStorage(LocalVariable localVariable, StackFrame stackFrame) throws DebugException {
        LOGGER.info(String.format("Handling local variable: %s", localVariable));

        Type type;
        try {
            type = localVariable.type();
        } catch (ClassNotLoadedException e) {
            LOGGER.severe(String.format("Failed to get type, %s", e));
            throw new DebugException("Failed to get type", e);
        }
        Value value = stackFrame.getValue(localVariable);

        init(localVariable.name(), type, value, VariableType.LOCAL_VARIABLE);
    }
}
