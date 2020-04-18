package pers.cheng.dij;


import org.junit.Test;

import junit.framework.TestCase;
import pers.cheng.dij.runner.DijSession;

import java.util.logging.Logger;

public class DijSessionTest {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);

    @Test
    public void testAll() {
        String[] classPaths = DijTestUtility.getClassPaths();
        String[] modulePaths = DijTestUtility.getModulePaths();
        String programArguments = "";
        String vmArguments = "";

        Class<?>[] testedClasses = DijTestUtility.getTestedClasses();
        for (Class<?> testedClass : testedClasses) {
            String mainClass = testedClass.getName();
            String crashPath = DijTestUtility.generateCrashLog(testedClass);

            DijSession dijSession = new DijSession(mainClass, programArguments, vmArguments, classPaths, modulePaths, crashPath);
            boolean reproductionStatus = false;
            try {
                reproductionStatus = dijSession.reproduce();
            } catch (Exception e) {
                e.printStackTrace();
                TestCase.fail();
            }

            TestCase.assertTrue(reproductionStatus);

            LOGGER.info(String.format("The test reproduction result is: %s", dijSession.getReproductionResult()));
        }
    }
}
