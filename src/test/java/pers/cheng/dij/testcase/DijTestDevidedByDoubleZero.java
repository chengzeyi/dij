package pers.cheng.dij.testcase;

import pers.cheng.dij.DijTestUtility;

public class DijTestDevidedByDoubleZero {
    public static void main(String[] args) {
        double divisor = DijTestUtility.isTrigger(args) ? 0.0 : 1.0;
        // EXP
        double result = 1.0 / divisor;
    }
}
