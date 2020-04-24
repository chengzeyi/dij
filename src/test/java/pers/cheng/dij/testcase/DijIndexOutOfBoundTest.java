package pers.cheng.dij.testcase;

// import java.util.logging.Logger;

// import pers.cheng.dij.Configuration;
import pers.cheng.dij.DijTestUtility;

public class DijIndexOutOfBoundTest extends DijTestCase {
    // private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);

    public static void main(String[] args) {
        int[] arr = {1, 2};
        int idx = DijTestUtility.isTrigger(args) ? -1 : 1;
        // EXP
        int x = arr[idx];
        // LOGGER.info(String.format("%s ended, no exception occurs", DijTestIndexOutOfBound.class.getName()));
    }
}
