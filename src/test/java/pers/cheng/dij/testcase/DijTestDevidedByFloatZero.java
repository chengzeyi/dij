package pers.cheng.dij.testcase;

import pers.cheng.dij.DijTestUtility;

public class DijTestDevidedByFloatZero {
    public static void main(String[] args) {
        float divisor = DijTestUtility.isTrigger(args) ? 0.0f : 1.0f;
        // EXP
        float result = 1.0f / divisor;
    }
}
