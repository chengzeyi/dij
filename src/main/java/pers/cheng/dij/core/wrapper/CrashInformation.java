package pers.cheng.dij.core.wrapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import pers.cheng.dij.Configuration;
import pers.cheng.dij.core.DebugException;

public class CrashInformation {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);

    private static final String[] EXCLUDED_PREFIXS = {
        "java.",
        "com.sun."
    };

    private List<String> crashLines;

    private List<String> stackTrace;

    private String exceptionLine;

    private String breakpointClassName;

    private int breakpointLineNumber;

    public CrashInformation(String filename) throws IOException, DebugException {
        crashLines = new ArrayList<>();

        BufferedReader bufferedReader;
        bufferedReader = new BufferedReader(new FileReader(filename));
        String line = bufferedReader.readLine();
        while (line != null) {
            line = line.trim();
            if (!line.isBlank()) {
                crashLines.add(line);
            }
            line = bufferedReader.readLine();
        }
        bufferedReader.close();

        if (crashLines.isEmpty()) {
            exceptionLine = "";
        } else {
            exceptionLine = crashLines.get(0);
        }

        LOGGER.info(String.format("Exception line: %s", exceptionLine));

        if (crashLines.size() > 1) {
            stackTrace = crashLines.subList(1, crashLines.size());
            analyzeStackTrace(stackTrace);
        } else {
            stackTrace = new ArrayList<>();
        }
    }

    public CrashInformation(String filename, int targetFrame) throws IOException, DebugException {
        crashLines = new ArrayList<>();

        BufferedReader bufferedReader;
        bufferedReader = new BufferedReader(new FileReader(filename));
        String line = bufferedReader.readLine();
        while (line != null) {
            line = line.trim();
            if (!line.isBlank()) {
                crashLines.add(line);
            }
            line = bufferedReader.readLine();
        }
        bufferedReader.close();

        if (crashLines.isEmpty()) {
            exceptionLine = "";
        } else {
            exceptionLine = crashLines.get(0);
        }

        LOGGER.info(String.format("Exception line: %s", exceptionLine));

        if (crashLines.size() > 1) {
            stackTrace = crashLines.subList(1, crashLines.size());
            analyzeStackTrace(stackTrace, targetFrame);
        } else {
            stackTrace = new ArrayList<>();
        }
    }

    private void analyzeStackTrace(List<String> stackTrace) throws DebugException {
OUTER_LOOP:
        for (String stackTraceLine : stackTrace) {
            String[] split = stackTraceLine.split("\\s", 2);
            if (split.length < 2 || !split[0].equals("at")) {
                continue;
            }
            String stackTraceElement = split[1];
            for (String excludedPrefix : EXCLUDED_PREFIXS) {
                if (stackTraceElement.startsWith(excludedPrefix)) {
                    continue OUTER_LOOP;
                }
            }

            int leftParenPos = stackTraceElement.indexOf('(');
            int rightParenPos = stackTraceElement.lastIndexOf(')');
            if (leftParenPos < 0 || rightParenPos < 0 || leftParenPos > rightParenPos) {
                continue;
            }
            String fileAndLine = stackTraceElement.substring(leftParenPos + 1, rightParenPos);
            String[] fileAndLineSplit = fileAndLine.split(":");
            if (split.length < 2) {
                continue;
            }
            breakpointLineNumber = Integer.parseInt(fileAndLineSplit[fileAndLineSplit.length - 1]);

            String methodSignature = stackTraceElement.substring(0, leftParenPos);
            int lastDotPos = methodSignature.lastIndexOf('.');
            if (lastDotPos < 0) {
                continue;
            }

            breakpointClassName = methodSignature.substring(0, lastDotPos);
            return;
        }

        throw new DebugException(String.format("Failed to analyze stack trace %s", stackTrace));
    }

    private void analyzeStackTrace(List<String> stackTrace, int targetFrame) throws DebugException {
        if (targetFrame <= 0) {
            analyzeStackTrace(stackTrace);
        }
        if (targetFrame > stackTrace.size()) {
            throw new DebugException(String.format("Target frame index %d is out of bound", targetFrame));
        }

        try {
            String stackTraceLine = stackTrace.get(targetFrame - 1);
            String[] split = stackTraceLine.split("\\s", 2);
            if (split.length < 2 || !split[0].equals("at")) {
                throw new DebugException();
            }
            String stackTraceElement = split[1];

            int leftParenPos = stackTraceElement.indexOf('(');
            int rightParenPos = stackTraceElement.lastIndexOf(')');
            if (leftParenPos < 0 || rightParenPos < 0 || leftParenPos > rightParenPos) {
                throw new DebugException();
            }
            String fileAndLine = stackTraceElement.substring(leftParenPos + 1, rightParenPos);
            String[] fileAndLineSplit = fileAndLine.split(":");
            if (split.length < 2) {
                throw new DebugException();
            }
            breakpointLineNumber = Integer.parseInt(fileAndLineSplit[fileAndLineSplit.length - 1]);

            String methodSignature = stackTraceElement.substring(0, leftParenPos);
            int lastDotPos = methodSignature.lastIndexOf('.');
            if (lastDotPos < 0) {
                throw new DebugException();
            }

            breakpointClassName = methodSignature.substring(0, lastDotPos);
        } catch (DebugException e) {
            throw new DebugException(String.format("Failed to parse target frame %d of stack trace %s", targetFrame, stackTrace));
        }
    }

    public List<String> getCrashLines() {
        return crashLines;
    }

    public List<String> getStackTrace() {
        return stackTrace;
    }

    public String getExceptionLine() {
        return exceptionLine;
    }

    public String getBreakpointClassName() {
        return breakpointClassName;
    }

    public int getBreakpointLineNumber() {
        return breakpointLineNumber;
    }

    public boolean hasBreakpoint() {
        return breakpointClassName != null;
    }
}
