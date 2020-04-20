package pers.cheng.dij.core.wrapper;

import java.util.List;
import java.util.logging.Logger;

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

import pers.cheng.dij.Configuration;
import pers.cheng.dij.core.wrapper.formatter.TypeIdentifier;

public class LoopBreakpointEventHandler extends BreakpointEventHandler {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);

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
        LOGGER.info(String.format("Handling breakpointEvent: %s", breakpointEvent));

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
                LOGGER.severe(String.format("Cannot get stack frames from the target thread, %s", e));
                return false;
            }
            StackFrame topStackFrame = stackFrames.get(0);
            LOGGER.info("Got top stack frame from the target thread");
            return changeLocalVariableValue(topStackFrame, guessedLocalVariableValue, guessedLocalVariableClassName,
                    guessedLocalVariableName);
        }

        LOGGER.warning("No guessedLocalVariable left");
        return false;
    }

    private boolean changeLocalVariableValue(StackFrame stackFrame, Object guessedLocalVariableValue,
            String guessedLocalVariableClassName, String guessedLocalVariableName) {
        LOGGER.info(String.format("Trying to change the value of the local variable: %s", guessedLocalVariableName));

        List<LocalVariable> localVariables;
        try {
            localVariables = stackFrame.visibleVariables();
        } catch (AbsentInformationException e) {
            LOGGER.severe(String.format("Cannot get localVariables from the stack frame, %s", e));
            return false;
        }

        for (LocalVariable localVariable : localVariables) {
            if (localVariable.name().equals(guessedLocalVariableName)) {
                String signature;
                try {
                    signature = localVariable.type().signature();
                } catch (ClassNotLoadedException e) {
                    // Ignore it since the variable must be loaded.
                    LOGGER.warning(String.format("The class of localVariable %s is not loaded, %s", localVariable, e));
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
                        LOGGER.warning(String.format("Unsupported type identifier: %s", signature));
                        continue;
                }

                VirtualMachine virtualMachine = stackFrame.virtualMachine();
                Value localVariableRawValue = stackFrame.getValue(localVariable);
                try {
                    if (signature0 == TypeIdentifier.BYTE) {
                        if (guessedLocalVariableClassName.equals(Byte.TYPE.getName())) {
                            changedLocalVariableRawValue = ((PrimitiveValue) localVariableRawValue).byteValue();
                            Value newValue = virtualMachine.mirrorOf((byte) guessedLocalVariableValue);
                            stackFrame.setValue(localVariable, newValue);
                        } else {
                            LOGGER.severe(String.format("Incompatible type of guessedLocalVariable: %s, expected: %s",
                                    guessedLocalVariableClassName, Byte.TYPE.getName()));
                        }
                    } else if (signature0 == TypeIdentifier.CHAR) {
                        if (guessedLocalVariableClassName.equals(Character.TYPE.getName())) {
                            changedLocalVariableRawValue = ((PrimitiveValue) localVariableRawValue).charValue();
                            Value newValue = virtualMachine.mirrorOf((char) guessedLocalVariableValue);
                            stackFrame.setValue(localVariable, newValue);
                        } else {
                            LOGGER.severe(String.format("Incompatible type of guessedLocalVariable: %s, expected: %s",
                                    guessedLocalVariableClassName, Character.TYPE.getName()));
                        }
                    } else if (signature0 == TypeIdentifier.FLOAT) {
                        if (guessedLocalVariableClassName.equals(Float.TYPE.getName())) {
                            changedLocalVariableRawValue = ((PrimitiveValue) localVariableRawValue).floatValue();
                            Value newValue = virtualMachine.mirrorOf((float) guessedLocalVariableValue);
                            stackFrame.setValue(localVariable, newValue);
                        } else {
                            LOGGER.severe(String.format("Incompatible type of guessedLocalVariable: %s, expected: %s",
                                    guessedLocalVariableClassName, Float.TYPE.getName()));
                        }
                    } else if (signature0 == TypeIdentifier.DOUBLE) {
                        if (guessedLocalVariableClassName.equals(Double.TYPE.getName())) {
                            changedLocalVariableRawValue = ((PrimitiveValue) localVariableRawValue).doubleValue();
                            Value newValue = virtualMachine.mirrorOf((double) guessedLocalVariableValue);
                            stackFrame.setValue(localVariable, newValue);
                        } else {
                            LOGGER.severe(String.format("Incompatible type of guessedLocalVariable: %s, expected: %s",
                                    guessedLocalVariableClassName, Double.TYPE.getName()));
                        }
                    } else if (signature0 == TypeIdentifier.INT) {
                        if (guessedLocalVariableClassName.equals(Integer.TYPE.getName())) {
                            changedLocalVariableRawValue = ((PrimitiveValue) localVariableRawValue).intValue();
                            Value newValue = virtualMachine.mirrorOf((int) guessedLocalVariableValue);
                            stackFrame.setValue(localVariable, newValue);
                        } else {
                            LOGGER.severe(String.format("Incompatible type of guessedLocalVariable: %s, expected: %s",
                                    guessedLocalVariableClassName, Integer.TYPE.getName()));
                        }
                    } else if (signature0 == TypeIdentifier.LONG) {
                        if (guessedLocalVariableClassName.equals(Long.TYPE.getName())) {
                            changedLocalVariableRawValue = ((PrimitiveValue) localVariableRawValue).longValue();
                            Value newValue = virtualMachine.mirrorOf((long) guessedLocalVariableValue);
                            stackFrame.setValue(localVariable, newValue);
                        } else {
                            LOGGER.severe(String.format("Incompatible type of guessedLocalVariable: %s, expected: %s",
                                    guessedLocalVariableClassName, Long.TYPE.getName()));
                        }
                    } else if (signature0 == TypeIdentifier.SHORT) {
                        if (guessedLocalVariableClassName.equals(Short.TYPE.getName())) {
                            changedLocalVariableRawValue = ((PrimitiveValue) localVariableRawValue).shortValue();
                            Value newValue = virtualMachine.mirrorOf((short) guessedLocalVariableValue);
                            stackFrame.setValue(localVariable, newValue);
                        } else {
                            LOGGER.severe(String.format("Incompatible type of guessedLocalVariable: %s, expected: %s",
                                    guessedLocalVariableClassName, Short.TYPE.getName()));
                        }
                    } else if (signature0 == TypeIdentifier.BOOLEAN) {
                        changedLocalVariableRawValue = ((PrimitiveValue) localVariableRawValue).booleanValue();
                        if (guessedLocalVariableClassName.equals(Boolean.TYPE.getName())) {
                            Value newValue = virtualMachine.mirrorOf((boolean) guessedLocalVariableValue);
                            stackFrame.setValue(localVariable, newValue);
                        } else {
                            LOGGER.severe(String.format("Incompatible type of guessedLocalVariable: %s, expected: %s",
                                    guessedLocalVariableClassName, Boolean.TYPE.getName()));
                        }
                    } else {
                        // This can never happen.
                        LOGGER.severe("This line should be dead code");
                    }
                } catch (ClassNotLoadedException | InvalidTypeException e) {
                    // May never hanppen.
                    LOGGER.severe(String.format("Cannot set value for local variable: %s", guessedLocalVariableName));
                    break;
                }

                changedLocalVariableName = guessedLocalVariableName;
                changedLocalVariableClassName = guessedLocalVariableClassName;
                changedLocalVariableNewValue = guessedLocalVariableValue;

                LOGGER.info(String.format(
                        "Successfully changed the value of the localVariable, name: %s, className: %s, newValue: %s",
                        guessedLocalVariableName, guessedLocalVariableValue, guessedLocalVariableValue));
                return true;
            }
        }

        LOGGER.severe(String.format("No suitable local variable found for: %s", guessedLocalVariableName));
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
