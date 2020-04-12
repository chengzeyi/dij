package pers.cheng.dij.core.wrapper.object;

import com.sun.jdi.ClassObjectReference;
import com.sun.jdi.ReferenceType;

public class ClassObjectcore.wrapper extends Objectcore.wrapper {

    public ClassObjectcore.wrapper(ClassObjectReference objReference) {
        super(objReference);
    }

    public ReferenceType getReflectedType() {
        return objReference.referenceType();
    }
}
