package pers.cheng.dij;

// import java.io.FileInputStream;
// import java.io.IOException;
// import java.util.Properties;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DijSettings {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);
    private static DijSettings CURRENT = new DijSettings();

    static {
        LOGGER.setLevel(Level.WARNING);
    }

    // TODO: update logLevel and javaHome.
    private String logLevel = null;
    private String javaHome = null;

    public static DijSettings getCurrent() {
        return CURRENT;
    }

    // public static void updateSettings(String settings) {
    //     try {
    //         FileInputStream ip = new FileInputStream(settings);
    //         Properties properties = new Properties();
    //         properties.load(ip);

    //         CURRENT.setLogLevel(properties.getProperty("logLevel"));
    //         CURRENT.setJavaHome(properties.getProperty("javaHome"));
    //     } catch (IOException e) {
    //         LOGGER.severe(String.format("Invalid properties for DebugSettings: %s, %s", settings, e));
    //     }
    // }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
        LOGGER.setLevel(Level.parse(logLevel));
    }

    public String getJavaHome() {
        return javaHome;
    }

    public void setJavaHome(String javaHome) {
        this.javaHome = javaHome;
    }
}