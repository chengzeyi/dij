package pers.cheng.dij.core.object;

import com.sun.jdi.StringReference;

public class StringWrapper extends ObjectWrapper {
    StringWrapper(StringReference objReference) {
        super(objReference);
    }

    public String getValue() {
        return ((StringReference) objReference).value();
    }
}
