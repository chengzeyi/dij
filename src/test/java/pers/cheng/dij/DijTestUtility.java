package pers.cheng.dij;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.logging.Logger;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import pers.cheng.dij.testcase.*;

public class DijTestUtility {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);

    private static final String CRASH_LOG_DIR = Path.of("tmp", "log").toAbsolutePath().toString();
    static {
        new File(CRASH_LOG_DIR).mkdirs();
    }
    
    private static final String CRASH_LOG_EXT = ".log";

    private static final String[] CLASS_PATHS = {
        Path.of("target", "test-classes").toAbsolutePath().toString()
    };

    private static final String[] MODULE_PATHS = {};

    private static final Class<?>[] TESTED_CLASSES = {
        DijTestDevidedByZero.class,
        DijTestDevidedByFloatZero.class,
        DijTestDevidedByDoubleZero.class,
        DijTestBooleanOperation.class,
        DijTestIndexOutOfBound.class,
        DijTestStringIndexOutOfBound.class,
        DijTestNullPointer.class
    };

    public static Class<?>[] getTestedClasses() {
        return TESTED_CLASSES;
    }

    public static boolean isTrigger(String[] args) {
        for (String arg : args) {
            if (arg.equals("trigger")) {
                return true;
            }
        }
        return false;
    }

    public static String completeClassName(String className) {
        return "pers.cheng.dij." + className;
    }

    public static String[] getModulePaths() {
        return CLASS_PATHS;
    }

    public static String[] getClassPaths() {
        return MODULE_PATHS;
    }

    public static String generateCrashLog(Class<?> testCaseClass) {
        String className = testCaseClass.getName();
        String crashLogPath = String.join(File.separator, CRASH_LOG_DIR, className + CRASH_LOG_EXT);

        Method mainMethod;
        try {
            mainMethod = testCaseClass.getDeclaredMethod("main", String[].class);
        } catch (NoSuchMethodException | SecurityException e) {
            LOGGER.severe(String.format("Cannot get main method, %s", e));
            return null;
        }

        try {
            mainMethod.invoke(null, (Object) new String[] { "trigger" });
        } catch (InvocationTargetException e) {
            try {
                PrintStream ps = new PrintStream(crashLogPath);
                try {
                    e.getTargetException().printStackTrace(ps);
                } finally {
                    ps.close();
                }
                return crashLogPath;
            } catch (FileNotFoundException ex) {
                LOGGER.severe(String.format("Cannot open file for writing log, %s", e));
                return null;
            }
        } catch (IllegalAccessException e) {
            LOGGER.severe(String.format("The target main method is inaccessible, %s", e));
            return null;
        }

        return null;
    }
}
