package pers.cheng.dij;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import pers.cheng.dij.testcase.DijTestDevidedByZero;

public class DijTestUtilityTest {
    @Test
    public void testGenerateCrashLog() {
        Class<?>[] classes = {
            DijTestDevidedByZero.class
        };
        for (Class<?> cl : classes) {
            assertNotNull(DijTestUtility.generateCrashLog(cl));
        }
    }
}
