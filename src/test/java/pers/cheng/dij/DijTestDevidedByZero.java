package pers.cheng.dij;

public class DijTestDevidedByZero {
    public static void main(String[] args) {
        int divisor = DijTestUtility.getTrigger(args) ? 0 : 1;
        // EXP
        int result = 1 / divisor;
    }
}
