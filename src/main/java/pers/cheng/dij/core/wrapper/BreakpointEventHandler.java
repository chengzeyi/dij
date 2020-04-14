package pers.cheng.dij.core.wrapper;

import java.util.List;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.BreakpointEvent;

import io.reactivex.Observable;
import pers.cheng.dij.core.DebugEvent;
import pers.cheng.dij.core.wrapper.formatter.TypeIdentifier;

public class BreakpointEventHandler {
    private BreakpointContext breakpointContext = null;

    private BpHandlerStatus status = BpHandlerStatus.CONTEXT_UNBINDED;

    private boolean breakpointHitten = false;

    private String lastChangedLocalVariableName = null;
    private String lastChangedLocalVariableClassName = null;
    private Object lastChangedLocalVariableValue = null;

    public BreakpointEventHandler() {
        status = BpHandlerStatus.CONTEXT_UNBINDED;
    }

    public BpHandlerStatus getStatus() {
        return status;
    }

    public void setBreakpointEvents(Observable<DebugEvent> breakpointEvents) {
        breakpointHitten = false;

        lastChangedLocalVariableName = null;
        lastChangedLocalVariableClassName = null;
        lastChangedLocalVariableValue = null;

        // if (status == BpHandlerStatus.UNINITIALIZED) {
        //     throw new UnsupportedOperationException("This handler has not been initialized.");
        // }
        breakpointEvents.subscribe(debugEvent -> {
            BreakpointEvent breakpointEvent = (BreakpointEvent) debugEvent.getEvent();
            handleBreakpointEvent(breakpointEvent);
        });
    }

    private void handleBreakpointEvent(BreakpointEvent breakpointEvent) {
        if (breakpointHitten) {
            return;
        }
        breakpointHitten = true;

        // if (status == BpHandlerStatus.UNINITIALIZED) {
        //     throw new UnsupportedOperationException("This handler has not been initialized.");
        // }
        if (status == BpHandlerStatus.CONTEXT_UNBINDED) {
            // The first to run the virtual machine, need to get context information.
            breakpointContext = getBreakpointContext(breakpointEvent);
            if (breakpointContext.hasNextGuessedLocalVariableValue()) {
                status = BpHandlerStatus.CONTEXT_BINDED;
            } else {
                status = BpHandlerStatus.CANNOT_CONTINUE;
            }
        } else if (status == BpHandlerStatus.CONTEXT_BINDED) {
            // Try different values using context information.
            if (breakpointContext.hasNextGuessedLocalVariableValue()) {
                Object guessedLocalVariableValue = breakpointContext.nextGuessedLocalVariableValue();
                String guessedLocalVariableClassName = breakpointContext.getCurrentGuessedVariableClassName();
                String guessedLocalVariableName = breakpointContext.getCurrentGuessedVariableName();
                ThreadReference threadReference = breakpointEvent.thread();
                List<StackFrame> stackFrames = null;
                try {
                    stackFrames = threadReference.frames();
                } catch (IncompatibleThreadStateException e) {
                    // Cannot continue to change the value.
                }
                if (stackFrames != null) {
                    StackFrame topStackFrame = stackFrames.get(0);
                    changeLocalVariableValue(topStackFrame, guessedLocalVariableValue, guessedLocalVariableClassName,
                            guessedLocalVariableName);
                }
            } else {
                status = BpHandlerStatus.CANNOT_CONTINUE;
            }
        } else if (status == BpHandlerStatus.CONTEXT_BIND_FAILED) {
            // Failed to get breakpoint context, do nothing.
            // Currently this status wouldn't be touched.
        } else {
            // status == BpHandlerStatus.STOPPED
            // Stopped.
            // Just do nothing.
        }
    }

    private BreakpointContext getBreakpointContext(BreakpointEvent breakpointEvent) {
        ThreadReference threadReference = breakpointEvent.thread();
        List<StackFrame> stackFrames;
        try {
            stackFrames = threadReference.frames();
        } catch (IncompatibleThreadStateException e) {
            status = BpHandlerStatus.CONTEXT_BIND_FAILED;
            return null;
        }
        BreakpointContext breakpointContext = new BreakpointContext();
        breakpointContext.processTopStackFrame(stackFrames.get(0));
        return breakpointContext;
    }

    private void changeLocalVariableValue(StackFrame stackFrame, Object guessedLocalVariableValue,
            String guessedLocalVariableClassName, String guessedLocalVariableName) {
        List<LocalVariable> localVariables;
        try {
            localVariables = stackFrame.visibleVariables();
        } catch (AbsentInformationException e) {
            return;
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
                try {
                    if (signature0 == TypeIdentifier.BYTE) {
                        if (guessedLocalVariableClassName.equals(Byte.class.getName())) {
                            Value newValue = virtualMachine.mirrorOf((byte) guessedLocalVariableValue);
                            stackFrame.setValue(localVariable, newValue);
                        }
                    } else if (signature0 == TypeIdentifier.CHAR) {
                        if (guessedLocalVariableClassName.equals(Character.class.getName())) {
                            Value newValue = virtualMachine.mirrorOf((char) guessedLocalVariableValue);
                            stackFrame.setValue(localVariable, newValue);
                        }
                    } else if (signature0 == TypeIdentifier.FLOAT) {
                        if (guessedLocalVariableClassName.equals(Float.class.getName())) {
                            Value newValue = virtualMachine.mirrorOf((float) guessedLocalVariableValue);
                            stackFrame.setValue(localVariable, newValue);
                        }
                    } else if (signature0 == TypeIdentifier.DOUBLE) {
                        if (guessedLocalVariableClassName.equals(Double.class.getName())) {
                            Value newValue = virtualMachine.mirrorOf((double) guessedLocalVariableValue);
                            stackFrame.setValue(localVariable, newValue);
                        }
                    } else if (signature0 == TypeIdentifier.INT) {
                        if (guessedLocalVariableClassName.equals(Integer.class.getName())) {
                            Value newValue = virtualMachine.mirrorOf((int) guessedLocalVariableValue);
                            stackFrame.setValue(localVariable, newValue);
                        }
                    } else if (signature0 == TypeIdentifier.LONG) {
                        if (guessedLocalVariableClassName.equals(Long.class.getName())) {
                            Value newValue = virtualMachine.mirrorOf((long) guessedLocalVariableValue);
                            stackFrame.setValue(localVariable, newValue);
                        }
                    } else if (signature0 == TypeIdentifier.SHORT) {
                        if (guessedLocalVariableClassName.equals(Short.class.getName())) {
                            Value newValue = virtualMachine.mirrorOf((short) guessedLocalVariableValue);
                            stackFrame.setValue(localVariable, newValue);
                        }
                    } else if (signature0 == TypeIdentifier.BOOLEAN) {
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
                lastChangedLocalVariableName = guessedLocalVariableName;
                lastChangedLocalVariableClassName = guessedLocalVariableClassName;
                lastChangedLocalVariableValue = guessedLocalVariableValue;
                break;
            }
        }
    }

    public String getLastChangedLocalVariableName() {
        return lastChangedLocalVariableName;
    }

    public String getLastChangedLocalVariableClassName() {
        return lastChangedLocalVariableClassName;
    }

    public Object getLastChangedLocalVariableValue() {
        return lastChangedLocalVariableValue;
    }
}
