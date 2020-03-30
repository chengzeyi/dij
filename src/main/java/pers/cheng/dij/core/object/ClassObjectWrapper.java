package pers.cheng.dij.core.object;

import com.sun.jdi.ClassObjectReference;
import com.sun.jdi.ObjectReference;

public class ClassObjectWrapper extends ObjectWrapper {

    ClassObjectWrapper(ClassObjectReference objReference) {
        super(objReference);
    }
}
