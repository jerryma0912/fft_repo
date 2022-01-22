package cn.hyperchain.business;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Demo {

    private static SerialTool serialTool;
    private static int serialRecvCnt = 0;
    public static ChartPanel chartPanel;

    private static String version = "1.0.0";
    public static void main(String[] param) {

        // 新建串口
        serialTool = new SerialTool();
        // 新建绘图数据集
        RealDataSet realDataSet = new RealDataSet();
        // 新建前端
        chartPanel = new ChartPanel("血氧监测工具", realDataSet, serialTool, version);
        chartPanel.pack();
        chartPanel.setVisible(true);
//        serialTool.OpenSerialTool(param[0]);

        while(true) {
            // 若队列小于20 这说明数据肯定没有收满
            if(serialTool.getSize() < 20) {
                continue;
            }
            // 找包头
            while(serialTool.getSize() > 1) {
                if(serialTool.at(0) != (byte)0xff || serialTool.at(1) != (byte)0xaa) {
                    serialTool.getNextByte();
                    serialRecvCnt += 1;
                } else {
                    break;
                }
            }
            // 检查真实有效数据的长度
            while(serialTool.getSize() >= 20) {
                Byte[] bytes = new Byte[20];
                for(int i=0; i<20; i++) {
                    bytes[i] = serialTool.getNextByte();
                }
                // 校验信号状态
                realDataSet.originPut(bytes[3], bytes[12]);
                serialRecvCnt += 20;
            }
            //System.out.println(serialRecvCnt);
        }

    }
}
