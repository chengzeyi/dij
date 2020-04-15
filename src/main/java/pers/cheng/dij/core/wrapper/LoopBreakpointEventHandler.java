package pers.cheng.dij.core.wrapper;

import java.util.List;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.PrimitiveValue;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.BreakpointEvent;

import pers.cheng.dij.core.wrapper.formatter.TypeIdentifier;

public class LoopBreakpointEventHandler extends BreakpointEventHandler {
    private String changedLocalVariableName;
    private String changedLocalVariableClassName;
    private Object changedLocalVariableRawValue;
    private Object changedLocalVariableNewValue;

    private BreakpointContext breakpointContext;

    public LoopBreakpointEventHandler(BreakpointContext breakpointContext) {
        this.breakpointContext = breakpointContext;
    }

    public boolean hasNextLoop() {
        return breakpointContext.hasNextGuessedLocalVariableValue();
    }

    protected boolean handleBreakpointEvent(BreakpointEvent breakpointEvent) {
        // Try different values using context information.
        if (breakpointContext.hasNextGuessedLocalVariableValue()) {
            Object guessedLocalVariableValue = breakpointContext.nextGuessedLocalVariableValue();
            String guessedLocalVariableClassName = breakpointContext.getCurrentGuessedVariableClassName();
            String guessedLocalVariableName = breakpointContext.getCurrentGuessedVariableName();
            ThreadReference threadReference = breakpointEvent.thread();
            List<StackFrame> stackFrames;
            try {
                stackFrames = threadReference.frames();
            } catch (IncompatibleThreadStateException e) {
                // Cannot continue to change the value.
                return false;
            }
            StackFrame topStackFrame = stackFrames.get(0);
            return changeLocalVariableValue(topStackFrame, guessedLocalVariableValue, guessedLocalVariableClassName,
                    guessedLocalVariableName);

        } else {
            return false;
        }
    }

    private boolean changeLocalVariableValue(StackFrame stackFrame, Object guessedLocalVariableValue,
            String guessedLocalVariableClassName, String guessedLocalVariableName) {
        List<LocalVariable> localVariables;
        try {
            localVariables = stackFrame.visibleVariables();
        } catch (AbsentInformationException e) {
            return false;
        }
        for (LocalVariable localVariable : localVariables) {
            if (localVariable.name().equals(guessedLocalVariableName)) {
                String signature;
                try {
                    signature = localVariable.type().signature();
                } catch (ClassNotLoadedException e) {
                    // Ignore it since the variable must be loaded.
                    continue;
                }
                char signature0 = signature.charAt(0);
                switch (signature0) {
                    case TypeIdentifier.BYTE:
                    case TypeIdentifier.CHAR:
                    case TypeIdentifier.FLOAT:
                    case TypeIdentifier.DOUBLE:
                    case TypeIdentifier.INT:
                    case TypeIdentifier.LONG:
                    case TypeIdentifier.SHORT:
                    case TypeIdentifier.BOOLEAN:
                        break;
                    default:
                        continue;
                }
                VirtualMachine virtualMachine = stackFrame.virtualMachine();
                Value localVariableRawValue = stackFrame.getValue(localVariable);
                try {
                    if (signature0 == TypeIdentifier.BYTE) {
                        if (guessedLocalVariableClassName.equals(Byte.class.getName())) {
                            changedLocalVariableRawValue = ((PrimitiveValue) localVariableRawValue).byteValue();
                            Value newValue = virtualMachine.mirrorOf((byte) guessedLocalVariableValue);
                            stackFrame.setValue(localVariable, newValue);
                        }
                    } else if (signature0 == TypeIdentifier.CHAR) {
                        if (guessedLocalVariableClassName.equals(Character.class.getName())) {
                            changedLocalVariableRawValue = ((PrimitiveValue) localVariableRawValue).charValue();
                            Value newValue = virtualMachine.mirrorOf((char) guessedLocalVariableValue);
                            stackFrame.setValue(localVariable, newValue);
                        }
                    } else if (signature0 == TypeIdentifier.FLOAT) {
                        if (guessedLocalVariableClassName.equals(Float.class.getName())) {
                            changedLocalVariableRawValue = ((PrimitiveValue) localVariableRawValue).floatValue();
                            Value newValue = virtualMachine.mirrorOf((float) guessedLocalVariableValue);
                            stackFrame.setValue(localVariable, newValue);
                        }
                    } else if (signature0 == TypeIdentifier.DOUBLE) {
                        if (guessedLocalVariableClassName.equals(Double.class.getName())) {
                            changedLocalVariableRawValue = ((PrimitiveValue) localVariableRawValue).doubleValue();
                            Value newValue = virtualMachine.mirrorOf((double) guessedLocalVariableValue);
                            stackFrame.setValue(localVariable, newValue);
                        }
                    } else if (signature0 == TypeIdentifier.INT) {
                        if (guessedLocalVariableClassName.equals(Integer.class.getName())) {
                            changedLocalVariableRawValue = ((PrimitiveValue) localVariableRawValue).intValue();
                            Value newValue = virtualMachine.mirrorOf((int) guessedLocalVariableValue);
                            stackFrame.setValue(localVariable, newValue);
                        }
                    } else if (signature0 == TypeIdentifier.LONG) {
                        if (guessedLocalVariableClassName.equals(Long.class.getName())) {
                            changedLocalVariableRawValue = ((PrimitiveValue) localVariableRawValue).longValue();
                            Value newValue = virtualMachine.mirrorOf((long) guessedLocalVariableValue);
                            stackFrame.setValue(localVariable, newValue);
                        }
                    } else if (signature0 == TypeIdentifier.SHORT) {
                        if (guessedLocalVariableClassName.equals(Short.class.getName())) {
                            changedLocalVariableRawValue = ((PrimitiveValue) localVariableRawValue).shortValue();
                            Value newValue = virtualMachine.mirrorOf((short) guessedLocalVariableValue);
                            stackFrame.setValue(localVariable, newValue);
                        }
                    } else if (signature0 == TypeIdentifier.BOOLEAN) {
                            changedLocalVariableRawValue = ((PrimitiveValue) localVariableRawValue).booleanValue();
                        if (guessedLocalVariableClassName.equals(Boolean.class.getName())) {
                            Value newValue = virtualMachine.mirrorOf((boolean) guessedLocalVariableValue);
                            stackFrame.setValue(localVariable, newValue);
                        }
                    } else {
                        // This can never happen.
                    }
                } catch (ClassNotLoadedException | InvalidTypeException e) {
                    // May never hanppen.
                    break;
                }
                changedLocalVariableName = guessedLocalVariableName;
                changedLocalVariableClassName = guessedLocalVariableClassName;
                changedLocalVariableNewValue = guessedLocalVariableValue;
                return true;
            }
        }
        return false;
    }

    public String getChangedLocalVariableName() {
        return changedLocalVariableName;
    }

    public String getChangedLocalVariableClassName() {
        return changedLocalVariableClassName;
    }

    public Object getChangedLocalVariableNewValue() {
        return changedLocalVariableNewValue;
    }

    public Object getChangedLocalVariableRawValue() {
        return changedLocalVariableRawValue;
    }
}
