package pers.cheng.dij.core.wrapper;

import java.util.logging.Logger;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.StackFrame;
import com.sun.jdi.Value;

import pers.cheng.dij.Configuration;
import pers.cheng.dij.core.DebugException;

public class LocalVariableStorage extends VariableStorage {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);

    public LocalVariableStorage(LocalVariable localVariable, StackFrame stackFrame) throws DebugException {
        LOGGER.info(String.format("Handling local variable: %s", localVariable));
        String variableName = localVariable.name();

        String variableTypeSignature;
        try {
            variableTypeSignature = localVariable.type().signature();
        } catch (ClassNotLoadedException e) {
            throw new DebugException(e);
        }

        Value variableValue = stackFrame.getValue(localVariable);

        init(variableName, variableTypeSignature, variableValue);
    }
}
