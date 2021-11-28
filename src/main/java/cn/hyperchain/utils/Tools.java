package cn.hyperchain.utils;

import cn.hyperchain.types.Complex;
import cn.hyperchain.types.Max;
import org.junit.Test;

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


    public static Max solve(int low, int high, Complex[] d)
    {
        Max mm = new Max();
        if(low == high)
        {
            mm.max1 = d[low].abs();
            mm.max1Index = low;
            mm.max2 = -1;
            mm.max2Index = -1;
        }
        else if(low == high-1)
        {
            if (d[low].abs() < d[high].abs()) {
                mm.max1 = d[high].abs();
                mm.max1Index = high;
                mm.max2 = d[low].abs();
                mm.max2Index = low;
            }
            else {
                mm.max1 = d[low].abs();
                mm.max1Index = low;
                mm.max2 = d[high].abs();
                mm.max2Index = high;
            }
        }
        else
        {
            int mid = (low+high) / 2;
            Max m1 = solve(low, mid, d);
            Max m2 = solve(mid+1, high, d);
            if(m1.max1 > m2.max1)
            {
                mm.max1 = m1.max1;
                mm.max1Index = m1.max1Index;
                if(m1.max2 > m2.max1) {
                    mm.max2 = m1.max2;
                    mm.max2Index = m1.max2Index;
                }
                else {
                    mm.max2 =m2.max1;
                    mm.max2Index = m2.max1Index;
                }
            }
            else
            {
                mm.max1 = m2.max1;
                mm.max1Index = m2.max1Index;
                if(m2.max2 > m1.max1) {
                    mm.max2 = m2.max2;
                    mm.max2Index = m2.max2Index;
                }
                else {
                    mm.max2 = m1.max1;
                    mm.max2Index = m1.max1Index;
                }
            }
        }
        return mm;
    }

    @Test
    public void testSolve() {
        double[] a = {0,2,5,7,9,8,1,2,1,0,0};
        Complex[] b = new Complex[a.length];
        for(int i=0; i<a.length; i++) {
            b[i] = new Complex(a[i],0);
        }
        Max m = solve(2,7,b);
        System.out.println(m.max1+" "+ m.max2+" "+  m.max1Index+" "+  m.max2Index);
    }

}
