package pers.cheng.dij.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class DebugSettings {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);
    private static DebugSettings CURRENT = new DebugSettings();

    private String logLevel;
    private String javaHome;

    public static DebugSettings getCurrent() {
        return CURRENT;
    }

    public static void updateSettings(String jsonSettings) {
        try {
            FileInputStream ip = new FileInputStream(jsonSettings);
            Properties properties = new Properties();
            properties.load(ip);

            CURRENT.setLogLevel(properties.getProperty("logLevel"));
            CURRENT.setJavaHome(properties.getProperty("javaHome"));
        } catch (IOException e) {
            LOGGER.severe(String.format("Invalid config.properties for debugSettings: %s, %s", jsonSettings, e));
        }
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public String getJavaHome() {
        return javaHome;
    }

    public void setJavaHome(String javaHome) {
        this.javaHome = javaHome;
    }
}
