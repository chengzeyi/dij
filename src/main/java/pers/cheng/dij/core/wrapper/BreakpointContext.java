package pers.cheng.dij.core.wrapper;

import com.sun.jdi.StackFrame;

import pers.cheng.dij.core.DebugException;

public class BreakpointContext {
    private LocalVariableKVStorage localVariableKVStorage;

    public void processTopStackFrame(StackFrame topStackFrame) throws DebugException {
        localVariableKVStorage = new LocalVariableKVStorage(topStackFrame);
    }

    public boolean hasNextGuessedLocalVariableValue() {
        return localVariableKVStorage.hasNext();
    }

    public Object nextGuessedLocalVariableValue() {
        return localVariableKVStorage.next();
    }

    public String getCurrentGuessedVariableName() {
        return localVariableKVStorage.getCurrentVariableName();
    }

    public String getCurrentGuessedVariableClassName() {
        return localVariableKVStorage.getCurrentVariableClassName();
    }
}
