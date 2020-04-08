package pers.cheng.dij.wrapper.object;

import com.sun.jdi.StringReference;

public class StringWrapper extends ObjectWrapper {
    public StringWrapper(StringReference objReference) {
        super(objReference);
    }

    public String getValue() {
        return ((StringReference) objReference).value();
    }
}
