package pers.cheng.dij.core.wrapper.object;

import com.sun.jdi.*;
import pers.cheng.dij.core.wrapper.IValuecore.wrapper;
import pers.cheng.dij.core.Configuration;
import pers.cheng.dij.core.DebugException;

import java.util.logging.Logger;

public class Objectcore.wrapper implements IValuecore.wrapper {
    protected static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);
    protected ObjectReference objReference;

    public Objectcore.wrapper(ObjectReference objReference) {
        this.objReference = objReference;
    }

    public Value getValue(String fieldName) throws DebugException {
        return getValue(objReference.referenceType().fieldByName(fieldName));
    }

    public Value getValue(Field field) throws DebugException {
        try {
            return objReference.getValue(field);
        } catch (IllegalArgumentException e) {
            throw new DebugException(String.format("The field %s of ReferenceType %s is invalid.",
                    field.name(), objReference.referenceType().name()), e);
        }
    }

    public void setValue(String fieldName, Value value) throws DebugException {
        setValue(objReference.referenceType().fieldByName(fieldName), value);
    }

    public void setValue(Field field, Value value) throws DebugException {
        if (value == null) {
            return;
        }
        try {
            objReference.setValue(field, value);
        } catch (IllegalArgumentException e) {
            throw new DebugException(String.format("The field %s of ReferenceType %s is invalid.",
                    field.name(), objReference.referenceType().name()), e);
        } catch (InvalidTypeException e) {
            throw new DebugException(String.format("The value's type %s does not match the field's %s.",
                    value.type().name(), field.typeName()), e);
        } catch (ClassNotLoadedException e) {
            throw new DebugException(String.format("The field type %s has not been loaded.",
                    field.typeName()), e);
        } catch (VMCannotBeModifiedException e) {
            handleVMCannotBeModifiedException();
        }
    }

    public void enableCollection() {
        try {
            objReference.enableCollection();
        } catch (VMCannotBeModifiedException e) {
            handleVMCannotBeModifiedException();
        }
    }

    public void disableCollection() {
        try {
            objReference.disableCollection();
        } catch (VMCannotBeModifiedException e) {
            handleVMCannotBeModifiedException();
        }
    }

    public boolean canBeModified() {
        try {
            return !objReference.isCollected();
        } catch (VMCannotBeModifiedException e) {
            handleVMCannotBeModifiedException();
            return false;
        }
    }

    protected void handleVMCannotBeModifiedException() {
        LOGGER.warning("The target vm cannot be modified.");
    }

    @Override
    public Type getType() {
        return objReference.type();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Objectcore.wrapper)) {
            return super.equals(obj);
        }
        return objReference.equals(((Objectcore.wrapper) obj).objReference);
    }
}
