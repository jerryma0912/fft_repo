package cn.hyperchain.utils;

public class Tools {

    public static int findMaxVal(double referPoint, int WIN, Number[] nArray) {
        int start = Math.max((int)Math.floor(referPoint - WIN),1);
        int end = (int)Math.ceil(referPoint + WIN);
        double max = -1;
        int index = -1;
        for(int i = start; i<= end; i++) {
            if(nArray[i].doubleValue() > max) {
                max = nArray[i].doubleValue();
                index = i;
            }
        }
        return index;
    }
}
