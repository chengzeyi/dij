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

    private Variable changedVariableRaw;
    private Variable changedVariableNew;

    public ReproductionResult(String breakpointClassName, int breakpointLineNumber, Variable changedVariableRaw,
            Variable changedVariableNew) {
        this.breakpointClassName = breakpointClassName;
        this.breakpointLineNumber = breakpointLineNumber;
        this.changedVariableRaw = changedVariableRaw;
        this.changedVariableNew = changedVariableNew;
    }

    public void writeToFile(String path) {
        try {
            PrintStream ps = new PrintStream(path);
            try {
                ps.println("BREAKPOINT_CLASS_NAME:  " + breakpointClassName);
                ps.println("BREAKPOINT_LINE_NUMBER: " + breakpointLineNumber);
                ps.println("CHANGED_VARIABLE_RAW:   " + changedVariableRaw);
                ps.println("CHANGED_VARIABLE_NEW:   " + changedVariableNew);
            } finally {
                ps.close();
            }
        } catch (FileNotFoundException e) {
            LOGGER.severe(String.format("Cannot open file for writing log, %s", e));
        }
    }

    public void print() {
        System.out.println("+------------------------------Successful Reproduction---------------------------------+");
        System.out.println("BREAKPOINT_CLASS_NAME:  " + breakpointClassName);
        System.out.println("BREAKPOINT_LINE_NUMBER: " + breakpointLineNumber);
        System.out.println("CHANGED_VARIABLE_RAW:   " + changedVariableRaw);
        System.out.println("CHANGED_VARIABLE_NEW:   " + changedVariableNew);
        System.out.println("+--------------------------------------------------------------------------------------+");
    }

    @Override
    public String toString() {
        return "ReproductionResult [breakpointClassName=" + breakpointClassName + ", breakpointLineNumber="
                + breakpointLineNumber + ", changedVariableNew=" + changedVariableNew + ", changedVariableRaw="
                + changedVariableRaw + "]";
    }
}
