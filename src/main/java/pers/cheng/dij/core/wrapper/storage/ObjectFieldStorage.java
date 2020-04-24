package pers.cheng.dij.core.wrapper.storage;

import java.util.logging.Logger;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.Field;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.Type;
import com.sun.jdi.Value;

import pers.cheng.dij.Configuration;
import pers.cheng.dij.core.DebugException;
import pers.cheng.dij.core.wrapper.variable.VariableType;

public class ObjectFieldStorage extends VariableStorage {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);

    public ObjectFieldStorage(ObjectReference objectReference, Field field) throws DebugException {
        String fieldName = field.name();
        Type fieldType;
        try {
            fieldType = field.type();
        } catch (ClassNotLoadedException e) {
            LOGGER.severe(String.format("Cannot get field type for field %s, %s", field, e));
            throw new DebugException(String.format("Cannot get field type for field %s", field), e);
        }

        Value fieldValue;
        try {
            fieldValue = objectReference.getValue(field);
        } catch (IllegalArgumentException e) {
            LOGGER.severe(String.format("Cannot get field value for field %s, %s", field, e));
            throw new DebugException(String.format("Cannot get field value for field %s", field));
        }

        init(fieldName, fieldType, fieldValue, VariableType.THIS_OBJECT_FIELD);
    }
}
