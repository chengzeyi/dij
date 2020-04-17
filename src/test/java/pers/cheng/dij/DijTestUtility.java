package pers.cheng.dij;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DijTestUtility {
    private static final String CRASH_LOG_DIR = Path.of("resources", "log").toAbsolutePath().toString();
    private static final String CRASH_LOG_EXT = ".log";

    public static boolean getTrigger(String[] args) {
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
        return new String[] {};
    }

    public static String[] getClassPaths() {
        return new String[] { "target/classes" };
    }

    public static String generateCrashLog(Class<?> testCaseClass) {
        String className = testCaseClass.getName();
        String crashLogPath = String.join(File.separator, CRASH_LOG_DIR, className + CRASH_LOG_EXT);

        Method mainMethod;
        try {
            mainMethod = testCaseClass.getDeclaredMethod("main", String[].class);
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            return null;
        }

        try {
            mainMethod.invoke(null, (Object) new String[] { "trigger" });
        } catch (InvocationTargetException e) {
            PrintStream ps;
            try {
                ps = new PrintStream(crashLogPath);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
                return null;
            }
            e.getTargetException().printStackTrace(ps);
            ps.close();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }

        return crashLogPath;
    }
}