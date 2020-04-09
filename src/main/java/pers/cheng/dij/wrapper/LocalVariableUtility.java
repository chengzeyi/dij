package pers.cheng.dij.wrapper;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Type;

import pers.cheng.dij.wrapper.formatter.TypeIdentifier;

public class LocalVariableUtility {
    public static boolean isPrimitiveType(LocalVariable localVariable) {
        Type declaringType;
        try {
            declaringType = localVariable.type();
        } catch (ClassNotLoadedException e) {
            // Only reference types can have ClassNotLoadedException thrown.
            return false;
        }
        char signature0 = declaringType.signature().charAt(0);
        switch (signature0) {
            case TypeIdentifier.BYTE:
            case TypeIdentifier.CHAR:
            case TypeIdentifier.FLOAT:
            case TypeIdentifier.DOUBLE:
            case TypeIdentifier.INT:
            case TypeIdentifier.LONG:
            case TypeIdentifier.SHORT:
            case TypeIdentifier.BOOLEAN:
                return true;
            default:
                return false;
        }
    }

    public static boolean isStringObjectType(LocalVariable localVariable) {
        Type declaringType;
        try {
            declaringType = localVariable.type();
        } catch (ClassNotLoadedException e) {
            // Cannot determine the type.
            return false;
        }
        char signature0 = declaringType.signature().charAt(0);
        return signature0 == TypeIdentifier.STRING || declaringType.signature().equals(TypeIdentifier.STRING_SIGNATURE);
    }

    public static boolean isArrayObjectType(LocalVariable localVariable) {
        char signature0;
        try {
            signature0 = localVariable.type().signature().charAt(0);
        } catch (ClassNotLoadedException e) {
            // Cannot determine the type.
            return false;
        }
        return signature0 == TypeIdentifier.ARRAY;
    }

    public static boolean isPrimitiveArrayObjectType(LocalVariable localVariable) {
        if (!isArrayObjectType(localVariable)) {
            return false;
        }
        char signature1;
        try {
            signature1 = localVariable.type().signature().charAt(1);
        } catch (ClassNotLoadedException e) {
            // This cannot happen.
            return false;
        }
        switch (signature1) {
            case TypeIdentifier.BYTE:
            case TypeIdentifier.CHAR:
            case TypeIdentifier.FLOAT:
            case TypeIdentifier.DOUBLE:
            case TypeIdentifier.INT:
            case TypeIdentifier.LONG:
            case TypeIdentifier.SHORT:
            case TypeIdentifier.BOOLEAN:
                return true;
            default:
                return false;
        }
    }

    public static boolean isStringArrayObjectType(LocalVariable localVariable) {
        if (!isArrayObjectType(localVariable)) {
            return false;
        }
        String signatureTail;
        try {
            signatureTail = localVariable.type().signature().substring(1);
        } catch (ClassNotLoadedException e) {
            // This cannot happen.
            return false;
        }
        return signatureTail.charAt(0) == TypeIdentifier.STRING
            || signatureTail.equals(TypeIdentifier.STRING_SIGNATURE);
    }
}
