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
import com.sun.jdi.StringReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.BreakpointEvent;

import pers.cheng.dij.Configuration;
import pers.cheng.dij.core.DebugException;
import pers.cheng.dij.core.wrapper.formatter.TypeIdentifier;

public class ModificationBreakpointEventHandler extends BreakpointEventHandler {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);

    private String changedLocalVariableName;
    private String changedLocalVariableClassName;
    private Object changedLocalVariableRawValue;
    private Object changedLocalVariableNewValue;

    private BreakpointContext breakpointContext;

    public ModificationBreakpointEventHandler(BreakpointContext breakpointContext) {
        this.breakpointContext = breakpointContext;
    }

    public boolean hasNextLoop() {
        return breakpointContext.hasNextGuessedLocalVariableValue();
    }

    protected void handleBreakpointEvent(BreakpointEvent breakpointEvent) throws DebugException {
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
                throw new DebugException("Cannot get start frames from the target thread", e);
            }
            StackFrame topStackFrame = stackFrames.get(0);
            LOGGER.info("Got top stack frame from the target thread");
            changeLocalVariableValue(topStackFrame, guessedLocalVariableValue, guessedLocalVariableClassName,
                    guessedLocalVariableName);
        }

        LOGGER.warning("No guessedLocalVariable left");
        throw new DebugException("No guessedLocalVariable left");
    }

    private void changeLocalVariableValue(StackFrame stackFrame, Object guessedLocalVariableValue,
            String guessedLocalVariableClassName, String guessedLocalVariableName) throws DebugException {
        LOGGER.info(String.format("Trying to change the value of the local variable: %s", guessedLocalVariableName));

        List<LocalVariable> localVariables;
        try {
            localVariables = stackFrame.visibleVariables();
        } catch (AbsentInformationException e) {
            LOGGER.severe(String.format("Cannot get local variables from the stack frame, %s", e));
            throw new DebugException("Cannot get local variables from the stack frame", e);
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
                // Pre-check
                switch (signature0) {
                    case TypeIdentifier.BYTE:
                    case TypeIdentifier.CHAR:
                    case TypeIdentifier.FLOAT:
                    case TypeIdentifier.DOUBLE:
                    case TypeIdentifier.INT:
                    case TypeIdentifier.LONG:
                    case TypeIdentifier.SHORT:
                    case TypeIdentifier.BOOLEAN:
                    case TypeIdentifier.STRING:
                    case TypeIdentifier.OBJECT:
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
                    // If the signature is TypeIdentifier.STRING_SIGNATURE, it
                    // will be caught here, not in the 'Object' clause.
                    } else if (signature0 == TypeIdentifier.STRING || signature.equals(TypeIdentifier.STRING_SIGNATURE)) {
                        changedLocalVariableRawValue = ((StringReference) localVariableRawValue).value();
                        // A null value's type is always 'Object' indeed, which is the only exception.
                        if (guessedLocalVariableClassName.equals(String.class.getName()) || guessedLocalVariableValue == null) {
                            Value newValue = virtualMachine.mirrorOf((String) guessedLocalVariableValue);
                            stackFrame.setValue(localVariable, newValue);
                        } else {
                            LOGGER.severe(String.format("Incompatible type of guessedLocalVariable: %s, expected: %s",
                                    guessedLocalVariableClassName, String.class.getName()));
                        }
                    } else if (signature0 == TypeIdentifier.OBJECT) {
                        // Currently it is too complex to get the general value of any plain 'Object'.
                        changedLocalVariableRawValue = "unknown";
                        if (guessedLocalVariableClassName.equals(Object.class.getName())) {
                            // It's hard to create a 'Value' mirror except for 'null' for a plain 'Object' type.
                            stackFrame.setValue(localVariable, null);
                        }
                    } else {
                        // This should never happen.
                        LOGGER.severe("This line should be dead code");
                    }
                } catch (ClassNotLoadedException | InvalidTypeException e) {
                    // May never hanppen.
                    LOGGER.severe(String.format("Cannot set value for local variable: %s, %s", guessedLocalVariableName, e));
                    break;
                }

                changedLocalVariableName = guessedLocalVariableName;
                changedLocalVariableClassName = guessedLocalVariableClassName;
                changedLocalVariableNewValue = guessedLocalVariableValue;

                LOGGER.info(String.format(
                        "Successfully changed the value of the localVariable, name: %s, className: %s, newValue: %s",
                        guessedLocalVariableName, guessedLocalVariableValue, guessedLocalVariableValue));
            }
        }

        LOGGER.severe(String.format("No suitable local variable found for: %s", guessedLocalVariableName));
        throw new DebugException(String.format("No suitable local variable for: %s", guessedLocalVariableName));
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
