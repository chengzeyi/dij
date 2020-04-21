package pers.cheng.dij.core.wrapper;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.logging.Logger;

import pers.cheng.dij.Configuration;

public class ReproductionResult {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);

    private String breakpointClassName;
    private int breakpointLineNumber;

    private String changedLocalVariableName;
    private String changedLocalVariableClassName;
    private String changedLocalVariableRawValue;
    private String changedLocalVariableNewValue;

    public ReproductionResult(String breakpointClassName, int breakpointLineNumber, String changedLocalVariableName,
            String changedLocalVariableClassName, String changedLocalVariableRawValue,
            String changedLocalVariableNewValue) {
        this.breakpointClassName = breakpointClassName;
        this.breakpointLineNumber = breakpointLineNumber;
        this.changedLocalVariableName = changedLocalVariableName;
        this.changedLocalVariableClassName = changedLocalVariableClassName;
        this.changedLocalVariableRawValue = changedLocalVariableRawValue;
        this.changedLocalVariableNewValue = changedLocalVariableNewValue;
    }

    public void writeToFile(String path) {
        PrintStream ps;
        try {
            ps = new PrintStream(path);
        } catch (FileNotFoundException e) {
            LOGGER.severe(String.format("Cannot open file for writing log, %s", e));
            return;
        }
        ps.println("BREAKPOINT_CLASS_NAME:             " + breakpointClassName);
        ps.println("BREAKPOINT_LINE_NUMBER:            " + breakpointLineNumber);
        ps.println("CHANGED_LOCAL_VARIABLE_CLASS_NAME: " + changedLocalVariableClassName);
        ps.println("CHANGED_LOCAL_VARIABLE_NAME:       " + changedLocalVariableName);
        ps.println("CHANGED_LOCAL_VARIABLE_RAW_VALUE:  " + changedLocalVariableRawValue);
        ps.println("CHANGED_LOCAL_VARIABLE_NEW_VALUE:  " + changedLocalVariableNewValue);
        ps.close();
    }

    public void print() {
        System.out.println("+------------------------------Successful Reproduction---------------------------------+");
        System.out.println("BREAKPOINT_CLASS_NAME:             " + breakpointClassName);
        System.out.println("BREAKPOINT_LINE_NUMBER:            " + breakpointLineNumber);
        System.out.println("CHANGED_LOCAL_VARIABLE_CLASS_NAME: " + changedLocalVariableClassName);
        System.out.println("CHANGED_LOCAL_VARIABLE_NAME:       " + changedLocalVariableName);
        System.out.println("CHANGED_LOCAL_VARIABLE_RAW_VALUE:  " + changedLocalVariableRawValue);
        System.out.println("CHANGED_LOCAL_VARIABLE_NEW_VALUE:  " + changedLocalVariableNewValue);
        System.out.println("+--------------------------------------------------------------------------------------+");
    }

    @Override
    public String toString() {
        return "ReproductionResult [breakpointClassName=" + breakpointClassName + ", breakpointLineNumber="
                + breakpointLineNumber + ", changedLocalVariableClassName=" + changedLocalVariableClassName
                + ", changedLocalVariableName=" + changedLocalVariableName + ", changedLocalVariableNewValue="
                + changedLocalVariableNewValue + ", changedLocalVariableRawValue=" + changedLocalVariableRawValue + "]";
    }
}
