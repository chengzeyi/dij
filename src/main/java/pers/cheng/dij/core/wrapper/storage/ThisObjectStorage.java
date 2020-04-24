package pers.cheng.dij.core.wrapper.storage;

import java.util.List;
import java.util.logging.Logger;

import com.sun.jdi.ClassNotPreparedException;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.InvalidStackFrameException;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;

import pers.cheng.dij.Configuration;
import pers.cheng.dij.core.DebugException;

public class ThisObjectStorage extends VariableKVStorage {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);

    public ThisObjectStorage(StackFrame stackFrame) throws DebugException {
        ObjectReference thisObject;
        try {
            thisObject = stackFrame.thisObject();
        } catch (InvalidStackFrameException e) {
            LOGGER.severe(String.format("Cannot get this object from the stack frame, %s", e));
            throw new DebugException("Cannot get this obeject fromt the stack frame", e);
        }

        if (thisObject == null) {
            LOGGER.warning(String.format("Cannot get this object, the stack frame %s represents a native or static method", stackFrame));
            throw new DebugException(String.format("Cannot get this object, the stack frame %s represents a native or static method", stackFrame));
        }

        ReferenceType referenceType = thisObject.referenceType();
        if (!(referenceType instanceof ClassType)) {
            throw new DebugException(String.format("Reference type %s is not ClassType", referenceType));
        }

        ClassType classType = (ClassType) referenceType;
        List<Field> fields;
        try {
            fields = classType.fields();
        } catch (ClassNotPreparedException e) {
            LOGGER.severe(String.format("Cannot get field from class type %s, %s", classType, e));
            throw new DebugException(String.format("Cannot get field from class type %s", classType), e);
        }

        for (Field field : fields) {
            ObjectFieldStorage objectFieldStorage;
            try {
                objectFieldStorage = new ObjectFieldStorage(thisObject, field);
            } catch (DebugException e) {
                LOGGER.warning(String.format("Object field %s is not cmpatible for analysis, %s", field, e));
                continue;
            }

            put(objectFieldStorage);
        }

        LOGGER.info(String.format("Got guessed values, total count: %d", guessedTotal));
    }
}
