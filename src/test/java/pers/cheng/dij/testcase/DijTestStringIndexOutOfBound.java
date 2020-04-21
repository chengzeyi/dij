package pers.cheng.dij.testcase;

// import java.util.logging.Logger;

// import pers.cheng.dij.Configuration;
import pers.cheng.dij.DijTestUtility;

public class DijTestStringIndexOutOfBound {
    // private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);

    public static void main(String[] args) {
        String str = DijTestUtility.isTrigger(args) ? "" : "this is a String";
        str.substring(1);
        // LOGGER.info(String.format("%s ended, no exception occurs", DijTestIndexOutOfBound.class.getName()));
    }
}
