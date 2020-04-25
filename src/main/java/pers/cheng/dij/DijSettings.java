package pers.cheng.dij;

// import java.io.FileInputStream;
// import java.io.IOException;
// import java.util.Properties;


public class DijSettings {
    private static DijSettings CURRENT = new DijSettings();

    // This might not be effective since this class might be loaded after
    // java.util.logging.Logger
    // static {
    //     LOGGER.setLevel(Level.WARNING);
    // }

    // private String logLevel = null;
    private String javaHome = null;
    private int editDistance = 0;
    private int timeout = 0;

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

	public int getEditDistance() {
        return editDistance;
    }

    public void setEditDistance(int editDistance) {
        this.editDistance = editDistance;
    }

    // public String getLogLevel() {
    //     return logLevel;
    // }

    // public void setLogLevel(String logLevel) {
    //     this.logLevel = logLevel;
    // }

    public String getJavaHome() {
        return javaHome;
    }

    public void setJavaHome(String javaHome) {
        this.javaHome = javaHome;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
