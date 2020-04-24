package pers.cheng.dij.core.wrapper.storage;

import java.util.List;
import java.util.logging.Logger;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.InvalidStackFrameException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.StackFrame;
import pers.cheng.dij.Configuration;
import pers.cheng.dij.core.DebugException;

public class LocalVariableKVStorage extends VariableKVStorage {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);

    public LocalVariableKVStorage(StackFrame stackFrame) throws DebugException {
        List<LocalVariable> localVariables;
        try {
            localVariables = stackFrame.visibleVariables();
        } catch (AbsentInformationException | InvalidStackFrameException e) {
            LOGGER.severe(String.format("Cannot get local variables from the stack frame, %s", e));
            throw new DebugException("Cannot get local variables from the stack frame", e);
        }

        for (LocalVariable localVariable : localVariables) {
            LocalVariableStorage localVariableStorage;
            try {
                localVariableStorage = new LocalVariableStorage(localVariable, stackFrame);
            } catch (DebugException e) {
                LOGGER.warning(String.format("Localvariable %s is not compatible for analysis, %s", localVariable, e));
                continue;
            }
            put(localVariableStorage);
        }

        LOGGER.info(String.format("Got guessed values, total count: %d", guessedTotal));
    }
}
