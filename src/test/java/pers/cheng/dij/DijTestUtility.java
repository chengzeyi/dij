package pers.cheng.dij;

public class DijTestUtility {
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
        return new String[]{};
    }

    public static String[] getClassPaths() {
        return new String[]{"target/classes"};
    }

    public static String generateCrashLog(String className) {
        String fullClassName = completeClassName(className);
    }
}
