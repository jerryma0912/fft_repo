package cn.hyperchain.utils;

import cn.hyperchain.types.Complex;

public class Tools {

    public static int findMaxVal(double referPoint, int WIN, Complex[] d) {
        int start = Math.max((int)Math.floor(referPoint - WIN),1);
        int end = (int)Math.ceil(referPoint + WIN);
        double max = -1;
        int index = -1;
        for(int i = start; i<= end; i++) {
            if(d[i].abs() > max) {
                max = d[i].abs();
                index = i;
            }
        }
        return index;
    }
}
