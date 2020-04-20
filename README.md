# Dij

Crash Reproduction via Modifying Runtime Object Values through Java Debug Interface

## General Algorithm

The Dij Application is based on a wrapper of Java Debug Interface (JDI). The core originated from Microsoft's `java-debug` project for their Visual Studio Code editor. This application would first launch the target Java program (the one been debugged) using `com.sun.tools.jdi.SunCommandLineLauncher`. And it would analyze the crash log file to obtain the exception information and infer the correct breakpoint position from the stack trace.

In the first launch of the target VM, Dij would set a breakpoint at the potential position of the root cause of the crash. After the target program triggered the breakpoint event, a brekpoint handler that is in subscription of this event would analyze the stack frame context of the breakpoint, recording the local variables (and, in the future, the `this` object). Based on the current value and type of these local variables, Dij would compute potential candidate values that could cause the crash. Because the original variable values in JDI are in fact mirrors of the target VM and would be invalid after the disconnection with it, Dij would copy and store them in its special data structures.

In the remaining loop of the launch of the target VM, Dij would set a breakpoint at the same position and an exception handler to figure out whether the reproduction is successful. After the breakpoint event is triggered, the breakpoint handler would retrieve a value of candidate variable from its data structures that store all the potential candidates and set the corresponding variable to the value. If an exception then happens and is not caught by the target VM, the exception handler would analyze the exception by comparing it with the exception information extracting fromt the crash log to decide the reproduction status. If the reproduction is successful, Dij would output the cause the crash (variable name, type, value, etc).

## Build

`mvn dependency:copy-dependencies`

`mvn package`

## Run

`java -jar target/dij-1.0-SNAPSHOT.jar [OPTIONS]... CRASH_LOG MAIN_CLASS [PROG_ARG]`

To see the help of options, run without any argument:

`java -jar target/dij-1.0-SNAPSHOT.jar`

To run tests:

`mvn test`

## Test

The Dij Application has its own test system. All test cases are located in `src/test/java/pers/cheng/dij/testcase/`. Each test case is a single Java class file with a `main` method defined. Each test case would not throw an exception if it is executed with out any argument. But if the arguments contain a String of `trigger`, then the exception would be thrown. The class `pers.cheng.dij.DijTestUtility` would use this feature to generate crash logs in `tmp/log` and run crash reproduction process.

## Road Map

- Support modifying non-primitive type variables.
- Support modifying `this` object in the stack frame.
- More accurate algorithms to analyze the breakpoint context information.

## Pros

- Extensibility: A very extensive framework to analyze and modify variables.
- Universalily: Can run any java program.
- Concurrency: Build upon an asynchronous framework.

## Limitions

- Currently only support primitive types.
- Does not support `this` object.
