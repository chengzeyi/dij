package pers.cheng.dij.testcase;

import pers.cheng.dij.DijTestUtility;

public class DijThisObjectTest extends DijTestCase {
    private int idx;

    public DijThisObjectTest() {

    }
    
    public static void main(String[] args) {
        DijThisObjectTest dijTestThisObject = new DijThisObjectTest();

        int idx = DijTestUtility.isTrigger(args) ? 10 : 0;
        dijTestThisObject.setIdx(idx);

        dijTestThisObject.retrieve();
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public int retrieve() {
        int[] arr = {1, 2, 3, 4, 5};
        return arr[idx];
    }
}
