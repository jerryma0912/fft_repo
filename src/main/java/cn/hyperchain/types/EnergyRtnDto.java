package cn.hyperchain.types;

public class EnergyRtnDto {

    private int index;
    private double freq;
    private double rms;
    private double phase;
    private Complex real;

    public int getIndex() {
        return index;
    }

    public double getFreq() {
        return freq;
    }

    public double getRms() {
        return rms;
    }

    public double getPhase() {
        return phase;
    }

    public Complex getReal() {
        return real;
    }

    public EnergyRtnDto(int index, double freq, double rms, double phase, Complex real) {
        this.index = index;
        this.freq = freq;
        this.rms = rms;
        this.phase = phase;
        this.real = real;
    }
}
