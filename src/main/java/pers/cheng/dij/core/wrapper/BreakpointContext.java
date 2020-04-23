package pers.cheng.dij.core.wrapper;

import com.sun.jdi.StackFrame;

import pers.cheng.dij.core.DebugException;
import pers.cheng.dij.core.wrapper.variable.Variable;

public class BreakpointContext {
    private LocalVariableKVStorage localVariableKVStorage;

    public void processTopStackFrame(StackFrame topStackFrame) throws DebugException {
        localVariableKVStorage = new LocalVariableKVStorage(topStackFrame);
    }

    public boolean hasNextGuessedLocalVariable() {
        return localVariableKVStorage.hasNext();
    }

    public Variable nextGuessedLocalVariable() {
        return localVariableKVStorage.next();
    }

    public String getGuessedVariableName() {
        return localVariableKVStorage.getCurrentVariableName();
    }

    public Variable getOriginalLocaVariable() {
        return localVariableKVStorage.getOriginalVariable();
    }
}
