package pers.cheng.dij.core.wrapper.handler;

import java.util.List;
import java.util.logging.Logger;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Type;
import com.sun.jdi.Value;
import com.sun.jdi.event.BreakpointEvent;

import pers.cheng.dij.Configuration;
import pers.cheng.dij.core.DebugException;
import pers.cheng.dij.core.wrapper.BreakpointContext;
import pers.cheng.dij.core.wrapper.handler.BreakpointEventHandler;
import pers.cheng.dij.core.wrapper.variable.Variable;
import pers.cheng.dij.core.wrapper.variable.VariableFormatter;

public class ModificationBreakpointEventHandler extends BreakpointEventHandler {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);

    private Variable changedLocalVariableRaw;
    private Variable changedLocalVariableNew;

    private BreakpointContext breakpointContext;

    public ModificationBreakpointEventHandler(BreakpointContext breakpointContext) {
        this.breakpointContext = breakpointContext;
    }

    public boolean hasNextLoop() {
        return breakpointContext.hasNextGuessedLocalVariable();
    }

    protected void handleBreakpointEvent(BreakpointEvent breakpointEvent) throws DebugException {
        LOGGER.info(String.format("Handling breakpointEvent: %s", breakpointEvent));

        // Try different values using context information.
        if (breakpointContext.hasNextGuessedLocalVariable()) {
            Variable guessedLocalVariable = breakpointContext.nextGuessedLocalVariable();
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
            changeLocalVariable(topStackFrame, guessedLocalVariable);

            changedLocalVariableRaw = breakpointContext.getOriginalLocaVariable();
            changedLocalVariableNew = guessedLocalVariable;
        }

        LOGGER.warning("No guessedLocalVariable left");
    }

    private void changeLocalVariable(StackFrame stackFrame, Variable guessedLocalVariable) throws DebugException {
        LOGGER.info(String.format("Trying to change the value of the local variable: %s", guessedLocalVariable));

        List<LocalVariable> localVariables;
        try {
            localVariables = stackFrame.visibleVariables();
        } catch (AbsentInformationException e) {
            LOGGER.severe(String.format("Cannot get local variables from the stack frame, %s", e));
            throw new DebugException("Cannot get local variables from the stack frame", e);
        }

        for (LocalVariable localVariable : localVariables) {
            if (localVariable.name().equals(guessedLocalVariable.getName())) {
                Type type;
                try {
                    type = localVariable.type();
                } catch (ClassNotLoadedException e) {
                    // Ignore it since the variable must be loaded.
                    LOGGER.warning(String.format("The class of local variable %s is not loaded, %s", localVariable, e));
                    continue;
                }

                String strType;
                try {
                    strType = VariableFormatter.typeToString(type);
                } catch (DebugException e) {
                    LOGGER.warning(String.format("Cannot parse Type %s to String, %s", type, e));
                    continue;
                }

                if (!strType.equals(guessedLocalVariable.getType())) {
                    LOGGER.warning(String.format("Type mismatch, current: %s, guessed: %s", strType, guessedLocalVariable.getType()));
                    continue;
                }

                Value guessedValue;
                try {
                    guessedValue = VariableFormatter.createVariableMirror(guessedLocalVariable, type);
                } catch (DebugException e) {
                    LOGGER.warning(String.format("Failed to create mirror in the target VM, variable: %s, type: %s", guessedLocalVariable, type));
                    throw new DebugException(String.format(
                                "Failed to create mirror in the target VM, variable: %s, type: %s", guessedLocalVariable, type), e);
                }

                try {
                    stackFrame.setValue(localVariable, guessedValue);
                } catch (InvalidTypeException | ClassNotLoadedException e) {
                    LOGGER.warning(String.format(
                                "Failed to set value for local variable: %s, guessed value: %s", localVariable, guessedValue));
                    continue;
                }

                LOGGER.info(String.format(
                        "Successfully changed the value of the local variable: %s, guessed: %s",
                        localVariable, guessedLocalVariable));

                return;
            }
        }

        LOGGER.severe(String.format("No suitable local variable found for: %s", guessedLocalVariable));
        throw new DebugException(String.format("No suitable local variable for: %s", guessedLocalVariable));
    }

    public Variable getChangedLocalVariableRaw() {
        return changedLocalVariableRaw;
    }

    public Variable getChangedLocalVariableNew() {
        return changedLocalVariableNew;
    }
}
