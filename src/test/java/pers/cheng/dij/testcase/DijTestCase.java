package pers.cheng.dij.testcase;

import org.junit.Test;

import pers.cheng.dij.DijTestUtility;

public abstract class DijTestCase {
    @Test
    public void test() {
        DijTestUtility.test(this.getClass());
    }
}
