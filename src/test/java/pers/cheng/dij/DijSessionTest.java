package pers.cheng.dij;

import java.util.logging.Logger;

import org.junit.Test;

import junit.framework.TestCase;
import pers.cheng.dij.core.wrapper.ReproductionResult;
import pers.cheng.dij.runner.DijSession;

public class DijSessionTest {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);

    static {
        DijSettings.getCurrent().setLogLevel("ALL");
    }

    @Test
    public void testAll() {
        String[] classPaths = DijTestUtility.getClassPaths();
        String[] modulePaths = DijTestUtility.getModulePaths();
        String programArguments = "";
        String vmArguments = "";

        Class<?>[] testedClasses = DijTestUtility.getTestedClasses();
        for (Class<?> testedClass : testedClasses) {
            LOGGER.info(String.format("Running reproduction for %s", testedClass));

            String mainClass = testedClass.getName();
            String crashPath = DijTestUtility.generateCrashLog(testedClass);

            TestCase.assertNotNull(crashPath);

            DijSession dijSession = new DijSession(mainClass, programArguments, vmArguments, classPaths, modulePaths, crashPath);

            try {
                dijSession.reproduce();
            } catch (Exception e) {
                e.printStackTrace();
                TestCase.fail();
            }

            TestCase.assertTrue(dijSession.isSuccessfulReproduction());

            dijSession.getReproductionResults().forEach(ReproductionResult::print);
        }
    }
}
