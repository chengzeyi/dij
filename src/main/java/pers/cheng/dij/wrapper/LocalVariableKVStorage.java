package pers.cheng.dij.wrapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.StackFrame;

public class LocalVariableKVStorage implements Iterator<Object> {
    // This class does not keep any instance in JDI.
    // Because the JDI instance might get killed.
    private Map<String, LocalVariableStorage> variableName2Storage = null;
    private Iterator<String> variableNameIter = null;
    private Iterator<Object> guessedValueIter = null;
    private int currentIdx = 0;
    private int guessedValueTotal = 0;

    public void init(StackFrame stackFrame) {
        variableName2Storage = new HashMap<>();
        List<LocalVariable> localVariables;
        try {
            localVariables = stackFrame.visibleVariables();
        } catch (AbsentInformationException e) {
            return;
        }
        for (LocalVariable localVariable : localVariables) {
            String variableName = localVariable.name();
            LocalVariableStorage localVariableStorage = new LocalVariableStorage();
            localVariableStorage.setInitialValue(localVariable, stackFrame);
            variableName2Storage.put(variableName, localVariableStorage);
            guessedValueTotal += localVariableStorage.getGuessedValueCount();
        }
    }

    @Override
    public boolean hasNext() {
        return currentIdx < guessedValueTotal;
    }

    @Override
    public Object next() {
        if (!hasNext()) {
            return null;
        }
        ++currentIdx;
        while (true) {
            if (variableNameIter == null) {
                variableNameIter = variableName2Storage.keySet().iterator();
            } else if (guessedValueIter.hasNext()) {
                return guessedValueIter.next();
            }
            String variableName = variableNameIter.next();
            guessedValueIter = variableName2Storage.get(variableName).getIterator();
        }
    }
}
