package pers.cheng.dij.core.wrapper;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.logging.Logger;

import pers.cheng.dij.Configuration;
import pers.cheng.dij.core.wrapper.variable.Variable;

public class ReproductionResult {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);

    private String breakpointClassName;
    private int breakpointLineNumber;

    private String changedLocalVariableName;
    private String changedLocalVariableType;
    private String changedLocalVariableRawValue;
    private String changedLocalVariableNewValue;

    public ReproductionResult(String breakpointClassName,
            int breakpointLineNumber, Variable localVariableRaw, Variable localVariableNew) {
        this.breakpointClassName = breakpointClassName;
        this.breakpointLineNumber = breakpointLineNumber;
        this.changedLocalVariableName = localVariableRaw.getName();
        this.changedLocalVariableType = localVariableRaw.getType();
        this.changedLocalVariableRawValue = localVariableRaw.getValue();
        this.changedLocalVariableNewValue = localVariableNew.getValue();
    }

    public void writeToFile(String path) {
        try {
            PrintStream ps = new PrintStream(path);
            try {
                ps.println("BREAKPOINT_CLASS_NAME:            " + breakpointClassName);
                ps.println("BREAKPOINT_LINE_NUMBER:           " + breakpointLineNumber);
                ps.println("CHANGED_LOCAL_VARIABLE_Type:      " + changedLocalVariableType);
                ps.println("CHANGED_LOCAL_VARIABLE_NAME:      " + changedLocalVariableName);
                ps.println("CHANGED_LOCAL_VARIABLE_RAW_VALUE: " + changedLocalVariableRawValue);
                ps.println("CHANGED_LOCAL_VARIABLE_NEW_VALUE: " + changedLocalVariableNewValue);
            } finally {
                ps.close();
            }
        } catch (FileNotFoundException e) {
            LOGGER.severe(String.format("Cannot open file for writing log, %s", e));
        }
    }

    public void print() {
        System.out.println("+------------------------------Successful Reproduction---------------------------------+");
        System.out.println("BREAKPOINT_CLASS_NAME:            " + breakpointClassName);
        System.out.println("BREAKPOINT_LINE_NUMBER:           " + breakpointLineNumber);
        System.out.println("CHANGED_LOCAL_VARIABLE_TYPE:      " + changedLocalVariableType);
        System.out.println("CHANGED_LOCAL_VARIABLE_NAME:      " + changedLocalVariableName);
        System.out.println("CHANGED_LOCAL_VARIABLE_RAW_VALUE: " + changedLocalVariableRawValue);
        System.out.println("CHANGED_LOCAL_VARIABLE_NEW_VALUE: " + changedLocalVariableNewValue);
        System.out.println("+--------------------------------------------------------------------------------------+");
    }

    @Override
    public String toString() {
        return "ReproductionResult [breakpointClassName=" + breakpointClassName + ", breakpointLineNumber="
                + breakpointLineNumber + ", changedLocalVariableClassName=" + changedLocalVariableType
                + ", changedLocalVariableName=" + changedLocalVariableName + ", changedLocalVariableNewValue="
                + changedLocalVariableNewValue + ", changedLocalVariableRawValue=" + changedLocalVariableRawValue + "]";
    }
}
