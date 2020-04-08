package pers.cheng.dij.wrapper.object;

import com.sun.jdi.ClassObjectReference;
import com.sun.jdi.ReferenceType;

public class ClassObjectWrapper extends ObjectWrapper {

    public ClassObjectWrapper(ClassObjectReference objReference) {
        super(objReference);
    }

    public ReferenceType getReflectedType() {
        return objReference.referenceType();
    }
}
