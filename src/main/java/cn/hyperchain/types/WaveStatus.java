package cn.hyperchain.types;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WaveStatus {
    private waveStatType waveStat = waveStatType.ERR;
    private int lastVal = 0;
    private int upCnt = 0;
    private int downCnt = 0;
    private final int WIN = 10;

    public WaveStatus() {
    }

    public void setErrStat() {
        lastVal = 0;
        upCnt = 0;
        downCnt = 0;
        waveStat = waveStatType.ERR;
    }

    public boolean updateStat(int data) {
        boolean res = false;
        int neg = data - lastVal;
        if(neg != 0) {
            if (neg > 0)  upCntAdd();
            else res = downCntAdd();
        }
        else {
            if(waveStat == waveStatType.UP) upCntAdd();
            else if(waveStat == waveStatType.DOWN) res = downCntAdd();
        }
        lastVal = data;
        //log.info("neg "+neg+" upCnt "+upCnt+" downCnt "+downCnt+"   waveStat="+waveStat);
        return res;
    }

    private void upCntAdd() {
        upCnt += 1;
        downCnt = 0;
        if(upCnt >= WIN && waveStat != waveStatType.UP) {
            waveStat = waveStatType.UP;
        }
    }

    private boolean downCntAdd() {
        downCnt += 1;
        upCnt = 0;
        if(downCnt >= WIN && waveStat != waveStatType.DOWN) {
            waveStat = waveStatType.DOWN;
            return true;
        }
        return false;
    }
}