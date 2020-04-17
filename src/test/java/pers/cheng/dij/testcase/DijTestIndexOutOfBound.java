package pers.cheng.dij.testcase;

import pers.cheng.dij.DijTestUtility;

public class DijTestIndexOutOfBound {
    public static void main(String[] args) {
        int[] arr = {1, 2};
        int idx = DijTestUtility.isTrigger(args) ? -1 : 1;
        // EXP
        int x = arr[idx];
    }
}
