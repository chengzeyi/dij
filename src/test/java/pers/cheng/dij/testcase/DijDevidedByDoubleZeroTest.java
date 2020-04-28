package pers.cheng.dij.testcase;

// import java.util.logging.Logger;

// import pers.cheng.dij.Configuration;
import pers.cheng.dij.DijTestUtility;

public class DijDevidedByDoubleZeroTest extends DijTestCase {
    // private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);

    public static void main(String[] args) {
        boolean t = true;
        boolean f = false;
        Object obj = new Object();
        int[] arr = new int[]{};

        double divisor = DijTestUtility.isTrigger(args) ? 0.0 : 1.0;
        // EXP
        int result = 1 / (int) divisor;
        // LOGGER.info(String.format("%s ended, no exception occurs", DijTestIndexOutOfBound.class.getName()));
    }
}
