package pers.cheng.dij.testcase;

import pers.cheng.dij.DijTestUtility;

public class DijTestBooleanOperation {
    public static void main(String[] args) {
        boolean cond = DijTestUtility.isTrigger(args) ? true : false;
        // EXP
        boolean result = cond && 1 / 0 > 0;
    }
}
