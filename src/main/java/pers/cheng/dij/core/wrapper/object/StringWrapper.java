package pers.cheng.dij.core.wrapper.object;

import com.sun.jdi.StringReference;

public class Stringcore.wrapper extends Objectcore.wrapper {
    public Stringcore.wrapper(StringReference objReference) {
        super(objReference);
    }

    public String getValue() {
        return ((StringReference) objReference).value();
    }
}
