package cn.hyperchain.utils;

import cn.hyperchain.types.Complex;
import lombok.extern.slf4j.Slf4j;

import static java.lang.Math.sin;

/******************************************************************************
        *  Compilation:  javac cn.hyperchain.utils.FFT.java
        *  Execution:    java cn.hyperchain.utils.FFT n
        *  Dependencies: cn.hyperchain.types.Complex.java
        *
        *  Compute the cn.hyperchain.utils.FFT and inverse cn.hyperchain.utils.FFT of a length n complex sequence
        *  using the radix 2 Cooley-Tukey algorithm.

        *  Bare bones implementation that runs in O(n log n) time and O(n)
        *  space. Our goal is to optimize the clarity of the code, rather
        *  than performance.
        *
        *  This implementation uses the primitive root of unity w = e^(-2 pi i / n).
        *  Some resources use w = e^(2 pi i / n).
        *
        *  Reference: https://www.cs.princeton.edu/~wayne/kleinberg-tardos/pdf/05DivideAndConquerII.pdf
        *
        *  Limitations
        *  -----------
        *   -  assumes n is a power of 2
        *
        *   -  not the most memory efficient algorithm (because it uses
        *      an object type for representing complex numbers and because
        *      it re-allocates memory for the subarray, instead of doing
        *      in-place or reusing a single temporary array)
        *
        *  For an in-place radix 2 Cooley-Tukey cn.hyperchain.utils.FFT, see
        *  https://introcs.cs.princeton.edu/java/97data/InplaceFFT.java.html
        *
        ******************************************************************************/

@Slf4j
public class FFT {

    // compute the cn.hyperchain.utils.FFT of x[], assuming its length n is a power of 2
    public static Complex[] fft(Complex[] x) {
        int n = x.length;

        // base case
        if (n == 1) return new Complex[] { x[0] };

        // radix 2 Cooley-Tukey cn.hyperchain.utils.FFT
        if (n % 2 != 0) {
            throw new IllegalArgumentException("n is not a power of 2");
        }

        // compute cn.hyperchain.utils.FFT of even terms
        Complex[] even = new Complex[n/2];
        for (int k = 0; k < n/2; k++) {
            even[k] = x[2*k];
        }
        Complex[] evenFFT = fft(even);

        // compute cn.hyperchain.utils.FFT of odd terms
        Complex[] odd  = even;  // reuse the array (to avoid n log n space)
        for (int k = 0; k < n/2; k++) {
            odd[k] = x[2*k + 1];
        }
        Complex[] oddFFT = fft(odd);

        // combine
        Complex[] y = new Complex[n];
        for (int k = 0; k < n/2; k++) {
            double kth = -2 * k * Math.PI / n;
            Complex wk = new Complex(Math.cos(kth), sin(kth));
            y[k]       = evenFFT[k].plus (wk.times(oddFFT[k]));
            y[k + n/2] = evenFFT[k].minus(wk.times(oddFFT[k]));
        }
        return y;
    }


    // compute the inverse cn.hyperchain.utils.FFT of x[], assuming its length n is a power of 2
    public static Complex[] ifft(Complex[] x) {
        int n = x.length;
        Complex[] y = new Complex[n];

        // take conjugate
        for (int i = 0; i < n; i++) {
            y[i] = x[i].conjugate();
        }

        // compute forward cn.hyperchain.utils.FFT
        y = fft(y);

        // take conjugate again
        for (int i = 0; i < n; i++) {
            y[i] = y[i].conjugate();
        }

        // divide by n
        for (int i = 0; i < n; i++) {
            y[i] = y[i].scale(1.0 / n);
        }

        return y;

    }

    // compute the circular convolution of x and y
    public static Complex[] cconvolve(Complex[] x, Complex[] y) {

        // should probably pad x and y with 0s so that they have same length
        // and are powers of 2
        if (x.length != y.length) {
            throw new IllegalArgumentException("Dimensions don't agree");
        }

        int n = x.length;

        // compute cn.hyperchain.utils.FFT of each sequence
        Complex[] a = fft(x);
        Complex[] b = fft(y);

        // point-wise multiply
        Complex[] c = new Complex[n];
        for (int i = 0; i < n; i++) {
            c[i] = a[i].times(b[i]);
        }

        // compute inverse cn.hyperchain.utils.FFT
        return ifft(c);
    }


    // compute the linear convolution of x and y
    public static Complex[] convolve(Complex[] x, Complex[] y) {
        Complex ZERO = new Complex(0, 0);

        Complex[] a = new Complex[2*x.length];
        for (int i = 0;        i <   x.length; i++) a[i] = x[i];
        for (int i = x.length; i < 2*x.length; i++) a[i] = ZERO;

        Complex[] b = new Complex[2*y.length];
        for (int i = 0;        i <   y.length; i++) b[i] = y[i];
        for (int i = y.length; i < 2*y.length; i++) b[i] = ZERO;

        return cconvolve(a, b);
    }

    // compute the DFT of x[] via brute force (n^2 time)
    public static Complex[] dft(Complex[] x) {
        int n = x.length;
        Complex ZERO = new Complex(0, 0);
        Complex[] y = new Complex[n];
        for (int k = 0; k < n; k++) {
            y[k] = ZERO;
            for (int j = 0; j < n; j++) {
                int power = (k * j) % n;
                double kth = -2 * power *  Math.PI / n;
                Complex wkj = new Complex(Math.cos(kth), sin(kth));
                y[k] = y[k].plus(x[j].times(wkj));
            }
        }
        return y;
    }

    // display an array of cn.hyperchain.types.Complex numbers to standard output
    public static void show(Complex[] x, String title) {
        for (int i = 0; i < x.length; i++) {
            if(x[i].abs()>1)
            log.info(i+" : "+x[i].abs());
        }
    }


    public static void main(String[] args) {
        int n = 1024;
        double PI = 3.14159265358979323846264338327950288;
        Complex[] x = new Complex[n];

        // original data
        for (int i = 0; i < n; i++) {
            double re =  1*sin(2*PI*240*i/1024.0f)+0.5*sin(2*PI*360*i/1024.0f) + 3;
            x[i] = new Complex(re, 0);
        }
        // cn.hyperchain.utils.FFT of original data
        Complex[] y = fft(x);
        show(y, "y = fft(x)");
    }

}