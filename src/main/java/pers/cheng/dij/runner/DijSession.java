package pers.cheng.dij.runner;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.VMStartException;

import io.reactivex.Observable;
import pers.cheng.dij.core.DebugEvent;
import pers.cheng.dij.core.DebugUtility;
import pers.cheng.dij.core.IBreakpoint;
import pers.cheng.dij.core.IDebugSession;
import pers.cheng.dij.core.IEventHub;
import pers.cheng.dij.core.wrapper.BreakpointContext;
import pers.cheng.dij.core.wrapper.CrashInformation;
import pers.cheng.dij.core.wrapper.ExceptionEventHandler;
import pers.cheng.dij.core.wrapper.FirstBreakpointEventHandler;
import pers.cheng.dij.core.wrapper.LoopBreakpointEventHandler;
import pers.cheng.dij.core.wrapper.ReproductionResult;

public class DijSession {
    private String mainClass;
    private String programArguments;
    private String vmArguments;
    private String modulePaths;
    private String classPaths;
    private String cwd;

    private String crashPath;

    private String breakpointClassName;
    private int breakpointLineNumber;

    private boolean reproductionSuccessful = false;
    private ReproductionResult reproductionResult;

    public DijSession(String mainClass, String programArguments, String vmArguments, List<String> modulePaths,
            List<String> classPaths, String cwd, String crashPath) {
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

    public DijSession(String mainClass, String programArguments, String vmArguments, List<String> modulePaths,
            List<String> classPaths, String crashPath) {
        this(mainClass, programArguments, vmArguments, modulePaths, classPaths, ".", crashPath);
    }

    public void setBreakpoint(String className, int lineNumber) {
        this.breakpointClassName = className;
        this.breakpointLineNumber = lineNumber;
    }

    public boolean reproduct() {
        CrashInformation crashInformation = new CrashInformation();
        try {
            crashInformation.parseCrashLinesFromFile(crashPath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        VirtualMachineManager vmManager = Bootstrap.virtualMachineManager();
        FirstBreakpointEventHandler firstBreakpointEventHandler = new FirstBreakpointEventHandler();

        IDebugSession debugSession;
        try {
            debugSession = DebugUtility.launch(vmManager, mainClass, programArguments, vmArguments, modulePaths,
                    classPaths, cwd);
        } catch (IOException | IllegalConnectorArgumentsException | VMStartException e) {
            e.printStackTrace();
            return false;
        }

        IBreakpoint breakpoint = debugSession.createBreakpoint(breakpointClassName, breakpointLineNumber);
        breakpoint.install();

        IEventHub eventHub = debugSession.getEventHub();

        Observable<DebugEvent> breakpointEvents = eventHub.getBreakpointEvents();
        firstBreakpointEventHandler.setBreakpointEvents(breakpointEvents);

        debugSession.start();
        debugSession.waitFor();

        if (!firstBreakpointEventHandler.isSuccessful()) {
            return false;
        }

        BreakpointContext breakpointContext = firstBreakpointEventHandler.getBreakpointContext();

        LoopBreakpointEventHandler loopBreakpointEventHandler = new LoopBreakpointEventHandler(breakpointContext);
        ExceptionEventHandler exceptionEventHandler = new ExceptionEventHandler(crashInformation);
        while (loopBreakpointEventHandler.hasNextLoop()) {
            try {
                debugSession = DebugUtility.launch(vmManager, mainClass, programArguments, vmArguments, modulePaths,
                        classPaths, cwd);
            } catch (IOException | IllegalConnectorArgumentsException | VMStartException e) {
                e.printStackTrace();
                return false;
            }
            breakpoint = debugSession.createBreakpoint(breakpointClassName, breakpointLineNumber);
            breakpoint.install();

            eventHub = debugSession.getEventHub();
            breakpointEvents = eventHub.getBreakpointEvents();
            loopBreakpointEventHandler.setBreakpointEvents(breakpointEvents);
            Observable<DebugEvent> exceptionEvents = eventHub.getExceptionEvents();
            exceptionEventHandler.setExceptionEvents(exceptionEvents);

            debugSession.setExceptionBreakpoints(false, true);

            debugSession.start();
            debugSession.waitFor();

            if (exceptionEventHandler.isReproductionSuccessful()) {
                reproductionSuccessful = true;
                String changedLocalVariableName = loopBreakpointEventHandler.getChangedLocalVariableName();
                String changedLocalVariableClassName = loopBreakpointEventHandler.getChangedLocalVariableClassName();
                String changedLocalVariableRawValue = Objects
                        .toString(loopBreakpointEventHandler.getChangedLocalVariableRawValue());
                String changedLocalVariableNewValue = Objects
                        .toString(loopBreakpointEventHandler.getChangedLocalVariableNewValue());
                reproductionResult = new ReproductionResult(changedLocalVariableName, changedLocalVariableClassName,
                        changedLocalVariableRawValue, changedLocalVariableNewValue);

                return true;
            }
        }

        return false;
    }

    public boolean isReproductionSuccessful() {
        return reproductionSuccessful;
    }

    public ReproductionResult getReproductionResult() {
        return reproductionResult;
    }
}
