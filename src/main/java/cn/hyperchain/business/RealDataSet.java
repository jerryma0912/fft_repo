package cn.hyperchain.business;

import cn.hyperchain.types.Complex;
import cn.hyperchain.types.WaveIndex;
import cn.hyperchain.types.WaveStatus;
import cn.hyperchain.utils.Tools;
import lombok.extern.slf4j.Slf4j;
import org.jfree.data.xy.XYSeries;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.PI;
import cn.hyperchain.utils.FFT;

@Slf4j
public class RealDataSet {

    // param
    private final int FFT_POINT_NUM = 1024;
    private final int SAMPLE_FREQ = 100;
    private final int WIN = 4;

    // 时域图像
    private final XYSeries originSeries = new XYSeries("origin");
    private int cnt = 0;
    // 频域图像
    private final XYSeries fftSeries = new XYSeries("fft");
    // 波形判断
    private final WaveStatus waveStatus = new WaveStatus();
    private final List<WaveIndex> waveIndexList = new ArrayList<>();
    private WaveIndex currentWaveIndex = null;
    // label
    private final JLabel j1 = new JLabel();

    public JLabel getJ1() {
        return j1;
    }

    public XYSeries getOriginSeries() {
        synchronized (originSeries) {
            return originSeries;
        }
    }

    public XYSeries getFftSeries() {
        synchronized (fftSeries) {
            return fftSeries;
        }
    }

    public RealDataSet() {}

    public void originPut(byte stat, int data) {
        if (stat == (byte)0) {
            synchronized (originSeries) {
                originSeries.add(cnt, data);
                cnt += 1;
                // 记录当前数据
                if(currentWaveIndex != null) {
                    currentWaveIndex.cntAdd();
                }
                // 判断波形状态
                boolean newWave = waveStatus.updateStat(data);
                if(newWave) {
                    // 总结上一波形
                    if(currentWaveIndex != null) {
                        currentWaveIndex.calVaild();
                        waveIndexList.add(currentWaveIndex);
                        log.info("summary: " + currentWaveIndex);
                    }
                    // 开启下一波形
                    currentWaveIndex = new WaveIndex(cnt);
                    calFft3();
                }
            }
        }
        else if(stat == (byte)2){
            //log.info("no finger");
            waveStatus.setErrStat();
            waveIndexList.clear();
            currentWaveIndex = null;
        }
    }

    private void calFft3() {
        // 计算从第几个波形开始计算fft
        int waveCnt = waveIndexList.size();
        if(waveCnt <= 0) {
            log.info("wavCnt = "+waveCnt+", 无整波");
            return;
        }
        int sum = 0;
        int needWave = -1;
        for(int i = 0; i<waveCnt; i++) {
            // 倒序查找
            sum += waveIndexList.get(waveCnt - i - 1).getCnt();
            if (sum >= FFT_POINT_NUM) {
                needWave = i + 1;
                break;
            }
        }
        if(needWave == -1) {
            log.info("所有波总点数 < " + FFT_POINT_NUM);
            return;
        }
        // 计算是否所有波均有效
        for(int i = needWave; i > 0; i--) {
            WaveIndex w = waveIndexList.get(waveCnt - i);
            if(!w.isVaild()) {
                log.info(w+" 是无效波");
                return;
            }
        }
        // 验证所有波是否超过fft需要的点
        WaveIndex startWave = waveIndexList.get(waveCnt - needWave);
        WaveIndex endWave = waveIndexList.get(waveCnt - 1);
        int len = endWave.getStartIndex() - startWave.getStartIndex() + endWave.getCnt();
        log.info("needWave = "+needWave+",len = " + len+" "+startWave+" "+endWave);
        if(len < FFT_POINT_NUM) {
            // 因为最后一个波是补足波，所以点数需要超过1024
            log.info("point cnt < " + FFT_POINT_NUM);
            return;
        }
        // 计算波点数的平均值
        int avgWavePointCnt = 0;
        for(int i = waveCnt - needWave; i <= waveCnt - 1; i++) {
            avgWavePointCnt += waveIndexList.get(i).getCnt();
        }
        avgWavePointCnt /= needWave;
        // 获取所有的点
        Complex[] x = new Complex[FFT_POINT_NUM];
        int startIndex = startWave.getStartIndex();
        log.info("start_index="+startIndex+" len="+len+" point_sum="+cnt+" start_wave="+startWave);
        for (int i = 0; i < FFT_POINT_NUM; i++) {
            double re = originSeries.getDataItem(startIndex+i).getYValue();
            x[i] = new Complex(re, 0);
        }
        Complex[] y = FFT.fft(x);
        setFftSeries(y,avgWavePointCnt);
    }

    public void setFftSeries(Complex[] d, int avgPoint) {

        if( d.length != FFT_POINT_NUM) {
            log.error("complex data len is not " + FFT_POINT_NUM);
            return;
        }
        // 计算所有的fft值，只计算半轴
        Number[] nArray = new Number[d.length / 2];
        Number[] pArray = new Number[d.length / 2];
        nArray[0] = d[0].abs() / FFT_POINT_NUM;
        pArray[0] = 0;
        for(int i=1; i < d.length / 2; i++) {
            nArray[i] = d[i].abs() * 2 / FFT_POINT_NUM;
            pArray[i] = d[i].phase() * 180 / PI;
        }
        // 根据平均点数计算参考频率及参考基波点
        double  referFreq = SAMPLE_FREQ / (double)avgPoint;
        double  referPoint = referFreq * FFT_POINT_NUM / SAMPLE_FREQ;
        // 计算峰值找到基波,范围是参考基波点上下2个值
        int index = Tools.findMaxVal(referPoint, WIN, nArray);
        double referAmp = nArray[index].doubleValue();
        double referPha = pArray[index].doubleValue();
        log.info("referAmp["+index+"] = "+referAmp+" referPha["+index+"] = "+referPha+" 参考点 = "+referPoint);
        // 根据基波幅值格式化
//        for(int i=0; i<d.length / 2; i++) {
//            nArray[i] = nArray[i].doubleValue() / referAmp;
//            pArray[i] = pArray[i].doubleValue() - referPha;
//        }
        // 设置动态图像
        synchronized (fftSeries) {
            fftSeries.clear();
            for(int i=1; i<d.length / 2; i++) {
                fftSeries.add((double)i * SAMPLE_FREQ / FFT_POINT_NUM, nArray[i]);
            }
        }
        // 打印各谐波的值
        String[] showName = {"基 波","2次谐波","3次谐波","4次谐波","5次谐波","6次谐波","7次谐波","8次谐波","9次谐波","10次谐波"};
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
        StringBuilder label = new StringBuilder();
        label.append("<html><body><table border=\"0\" width=\"800\">");
        for (int i=1; i<=10; i++) {
            label.append("<tr>");
            int i1 = Tools.findMaxVal(index * i, WIN, nArray);
            label.append("<td>").append(showName[i-1]).append(": </td>");
            label.append("<td> [点位索引] ").append(i1).append("</td>");
            label.append("<td> <font size=\"3\" color=\"green\">[频率] ").append(df.format((double)i1 * SAMPLE_FREQ / FFT_POINT_NUM)).append("Hz</font>").append("</td>");
            label.append("<td> <font size=\"3\" color=\"red\">[幅度] ").append(df.format(nArray[i1].doubleValue())).append("</font>").append("</td>");
            label.append("<td> <font size=\"3\" color=\"blue\">[相位] ").append(df.format(pArray[i1].doubleValue())).append("°</font>").append("</td>");
            label.append("<td> [实际值] ").append(d[i1]).append("</td>");
            label.append("<br/>");
            label.append("</tr>");
        }
        label.append("</table></body></html>");
        j1.setText(label.toString());
    }

}
