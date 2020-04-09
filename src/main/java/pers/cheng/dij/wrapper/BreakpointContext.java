package pers.cheng.dij.wrapper;

import java.util.ArrayList;
import java.util.List;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.StackFrame;

public class BreakpointContext {
    private StackFrame topStackFrame = null;
    private List<LocalVariable> localVariables = null;
    private ObjectReference thisObject = null;

    public void setTopStackFrame(StackFrame topStackFrame) {
        this.topStackFrame = topStackFrame;
        localVariables = getLocalVariables(topStackFrame);
        thisObject = getThisObject(topStackFrame);
    }

    private static List<LocalVariable> getLocalVariables(StackFrame stackFrame) {
        try {
            return stackFrame.visibleVariables();
        } catch (AbsentInformationException e) {
            return new ArrayList<>();
        }
    }

    private static ObjectReference getThisObject(StackFrame stackFrame) {
        return stackFrame.thisObject();
    }
}
