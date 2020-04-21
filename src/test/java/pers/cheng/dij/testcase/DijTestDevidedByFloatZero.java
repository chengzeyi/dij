package pers.cheng.dij.testcase;

import java.util.logging.Logger;

import pers.cheng.dij.Configuration;
import pers.cheng.dij.DijTestUtility;

public class DijTestDevidedByFloatZero {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);

    public static void main(String[] args) {
        float divisor = DijTestUtility.isTrigger(args) ? 0.0f : 1.0f;
        // EXP
        int result = 1 / (int) divisor;
        LOGGER.info(String.format("%s ended, no exception occurs", DijTestIndexOutOfBound.class.getName()));
    }
}
