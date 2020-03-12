package pers.cheng.dij.core;

import com.sun.jdi.*;

public class StackFrameUtility {
    public static boolean isNative(StackFrame frame) {
        return frame.location().method().isNative();
    }

    public static void pop(StackFrame frame) throws DebugException {
        try {
            frame.thread().popFrames(frame);
        } catch (IncompatibleThreadStateException e) {
            throw new DebugException(String.format("%s occurred popping stack frame.", e.getMessage(), e));
        } catch (InvalidStackFrameException e) {
            throw new DebugException("Cannot pop up the top stack frame.", e);
        } catch (NativeMethodException e) {
            throw new DebugException("Cannot pop up the stack frame because it is not valid for a native method.", e);
        } catch (RuntimeException e) {
            throw new DebugException(String.format("Runtime exception happened: %s", e.getMessage()), e);
        }
    }

    public static String getName(StackFrame frame) {
        return frame.location().method().name();
    }

    public static boolean isObsolete(StackFrame frame) {
        return frame.location().method().isObsolete();
    }

    public static String getSourcePath(StackFrame frame) {
        try {
            return frame.location().sourcePath();
        } catch (AbsentInformationException e) {
            // Ignore it
        }
        return null;
    }

    public static ReferenceType getDeclaringType(StackFrame frame) {
        return frame.location().method().declaringType();
    }
}
