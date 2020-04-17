package pers.cheng.dij;


import org.junit.Test;

import junit.framework.TestCase;
import pers.cheng.dij.runner.DijSession;

public class DijSessionTest {
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

            System.out.println(dijSession.getReproductionResult());
        }
    }
}
