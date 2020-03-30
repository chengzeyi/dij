package pers.cheng.dij.core.object;

import com.sun.jdi.*;
import pers.cheng.dij.core.DebugException;

public class ObjectWrapperBuilder {
    public static ObjectWrapper build(ObjectReference objReference) throws DebugException {
        if (objReference instanceof ArrayReference) {
            return new ArrayWrapper((ArrayReference) objReference);
        } else if (objReference instanceof StringReference) {
            return new StringWrapper((StringReference) objReference);
        } else if (objReference instanceof ClassLoaderReference ||
                objReference instanceof ClassObjectReference ||
                objReference instanceof ThreadGroupReference ||
                objReference instanceof ThreadReference) {
            throw new DebugException(String.format("Cannot build ObjectWrapper from Reference %s.",
                    objReference.referenceType().name()));
        } else {
            return new ObjectWrapper(objReference);
        }
    }
}
