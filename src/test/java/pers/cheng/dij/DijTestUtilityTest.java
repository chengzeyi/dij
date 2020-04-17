package pers.cheng.dij;

import org.junit.Test;

import junit.framework.TestCase;

public class DijTestUtilityTest {
    @Test
    public void testGenerateCrashLog() {
        Class<?>[] classes = DijTestUtility.getTestedClasses();
        for (Class<?> cl : classes) {
            TestCase.assertNotNull(DijTestUtility.generateCrashLog(cl));
        }
    }
}
