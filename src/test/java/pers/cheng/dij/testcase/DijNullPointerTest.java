package pers.cheng.dij.testcase;

// import java.util.logging.Logger;

// import pers.cheng.dij.Configuration;
import pers.cheng.dij.DijTestUtility;

public class DijNullPointerTest extends DijTestCase {
    // private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);
    
    public static void main(String[] args) {
        Object object = DijTestUtility.isTrigger(args) ? null : new Object();
        object.toString();
        // LOGGER.info(String.format("%s ended, no exception occurs", DijTestIndexOutOfBound.class.getName()));
    }
}
