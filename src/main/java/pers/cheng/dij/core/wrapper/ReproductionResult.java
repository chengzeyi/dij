package pers.cheng.dij.core.wrapper;

public class ReproductionResult {
    private String changedLocalVariableName;
    private String changedLocalVariableClassName;
    private String changedLocalVariableRawValue;
    private String changedLocalVariableNewValue;

    public ReproductionResult(String changedLocalVariableName, String changedLocalVariableClassName,
            String changedLocalVariableRawValue, String changedLocalVariableNewValue) {
        this.changedLocalVariableName = changedLocalVariableName;
        this.changedLocalVariableClassName = changedLocalVariableClassName;
        this.changedLocalVariableRawValue = changedLocalVariableRawValue;
        this.changedLocalVariableNewValue = changedLocalVariableNewValue;
    }

    @Override
    public String toString() {
        return String.format("{changedLocalVariableName: %s, changedLocalVariableClassName: %s, changedLocalVariableRawValue: %s, changedLocalVariableNewValue: %s}", changedLocalVariableName, changedLocalVariableClassName, changedLocalVariableRawValue, changedLocalVariableNewValue);
    }
}
