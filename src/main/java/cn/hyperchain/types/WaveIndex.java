package cn.hyperchain.types;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WaveIndex {
    // 本波形的起始索引，包括本索引
    private int startIndex;
    private boolean vaild = false;

    private int cnt = 0;

    private final int MAX_LEN = 300;

    public WaveIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public void cntAdd() {
        this.cnt += 1;
    }

    public void calVaild() {
        vaild = cnt <= MAX_LEN;
    }

    public boolean isVaild() {
        return vaild;
    }

    public int getCnt() {
        return cnt;
    }

    public int getStartIndex() {
        return startIndex;
    }

    @Override
    public String toString() {
        return "cn.hyperchain.business.WaveIndex{" +
                "startIndex=" + startIndex +
                ", vaild=" + vaild +
                ", cnt=" + cnt +
                ", MAX_LEN=" + MAX_LEN +
                '}';
    }
}
