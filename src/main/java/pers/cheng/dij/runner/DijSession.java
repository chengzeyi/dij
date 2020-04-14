package pers.cheng.dij.runner;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
import pers.cheng.dij.core.wrapper.BreakpointEventHandler;
import pers.cheng.dij.core.wrapper.CrashInformation;
import pers.cheng.dij.core.wrapper.ExceptionEventHandler;

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

    public void launch() {
        CrashInformation crashInformation = new CrashInformation();
        try {
            crashInformation.parseCrashLinesFromFile(crashPath);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        VirtualMachineManager vmManager = Bootstrap.virtualMachineManager();
        BreakpointEventHandler breakpointEventHandler = new BreakpointEventHandler();
        ExceptionEventHandler exceptionEventHandler = new ExceptionEventHandler(crashInformation);

        while (true) {
            IDebugSession debugSession;
            try {
                debugSession = DebugUtility.launch(vmManager, mainClass, programArguments, vmArguments,
                        modulePaths, classPaths, cwd);
            } catch (IOException | IllegalConnectorArgumentsException | VMStartException e) {
                e.printStackTrace();
                return;
            }
            
            IBreakpoint breakpoint = debugSession.createBreakpoint(breakpointClassName, breakpointLineNumber);
            breakpoint.install();

            IEventHub eventHub = debugSession.getEventHub();

            Observable<DebugEvent> breakpointEvents = eventHub.getBreakpointEvents();
            breakpointEventHandler.setBreakpointEvents(breakpointEvents);

            Observable<DebugEvent> exceptionEvents = eventHub.getExceptionEvents();
            exceptionEventHandler.setExceptionEvents(exceptionEvents);

            debugSession.start();
        }
    }
}
