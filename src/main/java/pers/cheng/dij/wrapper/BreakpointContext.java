package pers.cheng.dij.wrapper;

import com.sun.jdi.StackFrame;

public class BreakpointContext {
    private LocalVariableKVStorage localVariableKVStorage = null;

    public void processTopStackFrame(StackFrame topStackFrame) {
        localVariableKVStorage = new LocalVariableKVStorage();
        localVariableKVStorage.init(topStackFrame);
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
