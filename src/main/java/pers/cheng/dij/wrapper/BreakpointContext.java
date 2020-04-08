package pers.cheng.dij.wrapper;

import java.util.ArrayList;
import java.util.List;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.StackFrame;

public class BreakpointContext {
    private StackFrame topStackFrame = null;
    private List<LocalVariable> localVariables = null;

    public void setTopStackFrame(StackFrame topStackFrame) {
        this.topStackFrame = topStackFrame;
        localVariables = getLocalVariables(topStackFrame);
    }

    private static List<LocalVariable> getLocalVariables(StackFrame stackFrame) {
        try {
            return stackFrame.visibleVariables();
        } catch (AbsentInformationException e) {
            return new ArrayList<>();
        }
    }
}
