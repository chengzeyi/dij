package pers.cheng.dij.testcase;

// import java.util.logging.Logger;

// import pers.cheng.dij.Configuration;
import pers.cheng.dij.DijTestUtility;

public class DijStringIndexOutOfBoundTest extends DijTestCase {
    // private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);

    public static void main(String[] args) {
        boolean t = true;
        boolean f = false;
        Object obj = new Object();
        int[] arr = new int[]{};

        String str = DijTestUtility.isTrigger(args) ? "" : "this is a String";
        str.substring(1);
        // LOGGER.info(String.format("%s ended, no exception occurs", DijTestIndexOutOfBound.class.getName()));
    }
}
