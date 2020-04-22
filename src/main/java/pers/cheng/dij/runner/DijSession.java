package pers.cheng.dij.runner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.VMStartException;

import io.reactivex.Observable;
import pers.cheng.dij.Configuration;
import pers.cheng.dij.DijException;
import pers.cheng.dij.core.DebugEvent;
import pers.cheng.dij.core.DebugUtility;
import pers.cheng.dij.core.IBreakpoint;
import pers.cheng.dij.core.IDebugSession;
import pers.cheng.dij.core.IEventHub;
import pers.cheng.dij.core.wrapper.BreakpointContext;
import pers.cheng.dij.core.wrapper.CrashInformation;
import pers.cheng.dij.core.wrapper.ExceptionEventHandler;
import pers.cheng.dij.core.wrapper.PioneerBreakpointEventHandler;
import pers.cheng.dij.core.wrapper.ModificationBreakpointEventHandler;
import pers.cheng.dij.core.wrapper.ReproductionResult;

public class DijSession {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);
    private final String mainClass;
    private final String programArguments;
    private final String vmArguments;
    private final String modulePaths;
    private final String classPaths;
    private final String cwd;

    private final String crashPath;

    private String breakpointClassName;
    private int breakpointLineNumber;

    private boolean successfulReproduction = false;
    private List<ReproductionResult> reproductionResults = new ArrayList<>();

    public DijSession(String mainClass, String programArguments, String vmArguments, String[] modulePaths,
            String[] classPaths, String cwd, String crashPath) {
        this(mainClass, programArguments, vmArguments, String.join(File.pathSeparator, modulePaths),
                String.join(File.pathSeparator, classPaths), cwd, crashPath);
    }

    public DijSession(String mainClass, String programArguments, String vmArguments, String modulePaths,
            String classPaths, String cwd, String crashPath) {
        this.mainClass = mainClass;
        this.programArguments = programArguments;
        this.vmArguments = vmArguments;
        this.modulePaths = modulePaths;
        this.classPaths = classPaths;
        this.cwd = cwd;

        this.crashPath = crashPath;
    }

    public DijSession(String mainClass, String programArguments, String vmArguments, String modulePaths,
            String classPaths, String crashPath) {
        this(mainClass, programArguments, vmArguments, modulePaths, classPaths, ".", crashPath);
    }

    public DijSession(String mainClass, String programArguments, String vmArguments, String[] modulePaths,
            String[] classPaths, String crashPath) {
        this(mainClass, programArguments, vmArguments, modulePaths, classPaths, ".", crashPath);
    }

    public void setBreakpoint(String className, int lineNumber) {
        this.breakpointClassName = className;
        this.breakpointLineNumber = lineNumber;
    }

    public void reproduce() throws DijException {
        LOGGER.info(String.format(
                "Reproduction started, mainClass: %s, programArguments: %s, vmArguments: %s, modulePaths: %s, classPaths: %s, cwd: %s, crashPath: %s",
                mainClass, programArguments, vmArguments, modulePaths, classPaths, cwd, crashPath));

        CrashInformation crashInformation = new CrashInformation();
        try {
            crashInformation.parseCrashLinesFromFile(crashPath);
        } catch (IOException e) {
            LOGGER.severe(String.format("Cannot read crash lines from file, %s", e));
            throw new DijException(e);
        }

        if (breakpointClassName == null) {
            if (crashInformation.hasBreakpoint()) {
                setBreakpoint(crashInformation.getBreakpointClassName(), crashInformation.getBreakpointLineNumber());
            } else {
                LOGGER.severe(String.format("Cannot infer breakpoint position from crash log, path: %s", crashPath));
                throw new DijException();
            }
        }

        VirtualMachineManager vmManager = Bootstrap.virtualMachineManager();

        IDebugSession debugSession;
        try {
            debugSession = DebugUtility.launch(vmManager, mainClass, programArguments, vmArguments, modulePaths,
                    classPaths, cwd);
        } catch (IOException | IllegalConnectorArgumentsException | VMStartException e) {
            LOGGER.severe(String.format("Cannot launch debug session, %s", e));
            throw new DijException("Cannot launch debug session", e);
        }

        IBreakpoint breakpoint = debugSession.createBreakpoint(breakpointClassName, breakpointLineNumber);
        breakpoint.install();
        LOGGER.info(String.format("Breakpoint have been installed for debug session, className: %s, line: %d",
                breakpointClassName, breakpointLineNumber));

        IEventHub eventHub = debugSession.getEventHub();

        PioneerBreakpointEventHandler pioneerBreakpointEventHandler = new PioneerBreakpointEventHandler();
        Observable<DebugEvent> breakpointEvents = eventHub.getBreakpointEvents();
        pioneerBreakpointEventHandler.setBreakpointEvents(breakpointEvents);

        ExceptionEventHandler exceptionEventHandler = new ExceptionEventHandler(crashInformation);
        Observable<DebugEvent> exceptionEvents = eventHub.getExceptionEvents();
        exceptionEventHandler.setExceptionEvents(exceptionEvents);

        debugSession.setExceptionBreakpoints(false, true);
        LOGGER.info("Exception breakpoints have been set");

        debugSession.start();
        debugSession.waitFor();

        if (!pioneerBreakpointEventHandler.isSuccessful()) {
            LOGGER.severe("Failed to handle pioneerBreakpoint");
            throw new DijException("Failed to handle pioneerBreakpoint");
        }

        if (exceptionEventHandler.isReproductionSuccessful()) {
            LOGGER.severe("Please check the target program, the exception can be reproduced without modifying any variable");
        }

        BreakpointContext breakpointContext = pioneerBreakpointEventHandler.getBreakpointContext();

        LOGGER.info("Got breakpointContext from pioneerBreakpointEventHandler");

        ModificationBreakpointEventHandler modificationBreakpointEventHandler = new ModificationBreakpointEventHandler(breakpointContext);
        while (modificationBreakpointEventHandler.hasNextLoop()) {
            LOGGER.info("Trying to start a new debug loop");
            try {
                debugSession = DebugUtility.launch(vmManager, mainClass, programArguments, vmArguments, modulePaths,
                        classPaths, cwd);
            } catch (IOException | IllegalConnectorArgumentsException | VMStartException e) {
                LOGGER.severe("Failed to launch debug session.");
                throw new DijException("Failed to launch debug session", e);
            }

            breakpoint = debugSession.createBreakpoint(breakpointClassName, breakpointLineNumber);
            breakpoint.install();
            LOGGER.info(String.format("Breakpoint have been installed for debug session, className: %s, line: %d",
                    breakpointClassName, breakpointLineNumber));

            eventHub = debugSession.getEventHub();
            breakpointEvents = eventHub.getBreakpointEvents();
            modificationBreakpointEventHandler.setBreakpointEvents(breakpointEvents);

            exceptionEvents = eventHub.getExceptionEvents();
            exceptionEventHandler.setExceptionEvents(exceptionEvents);

            debugSession.setExceptionBreakpoints(false, true);
            LOGGER.info("Exception breakpoints have been set");

            debugSession.start();
            debugSession.waitFor();

            if (exceptionEventHandler.isReproductionSuccessful()) {
                LOGGER.info("The handler has successfully reproduced the crash");
                successfulReproduction = true;
                String changedLocalVariableName = modificationBreakpointEventHandler.getChangedLocalVariableName();
                String changedLocalVariableClassName = modificationBreakpointEventHandler.getChangedLocalVariableClassName();
                String changedLocalVariableRawValue = Objects
                        .toString(modificationBreakpointEventHandler.getChangedLocalVariableRawValue());
                String changedLocalVariableNewValue = Objects
                        .toString(modificationBreakpointEventHandler.getChangedLocalVariableNewValue());
                ReproductionResult reproductionResult = new ReproductionResult(breakpointClassName, breakpointLineNumber, changedLocalVariableName, changedLocalVariableClassName, changedLocalVariableRawValue, changedLocalVariableNewValue);
                reproductionResults.add(reproductionResult);
            }
        }

        if (!successfulReproduction) {
            LOGGER.severe(String.format("Reproduction failed, mainClass: %s", mainClass));
        }
    }

    public boolean isSuccessfulReproduction() {
        return successfulReproduction;
    }

    public List<ReproductionResult> getReproductionResults() {
        return reproductionResults;
    }
}
