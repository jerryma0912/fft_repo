package cn.hyperchain.business;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;

import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeriesCollection;

@Slf4j
public class ChartPanel extends javax.swing.JFrame {

    private JMenu comMenu = null;
    private JMenuItem flushMenuItem = null;

    private void flashMenu(SerialTool serialTool) {
        Boolean first_flag = true;
        // 清除老按钮
        comMenu.removeAll();
        comMenu.add(flushMenuItem);
        comMenu.addSeparator();
        // 添加新按钮
        ButtonGroup comButtonGroup = new ButtonGroup();
        List<String> l = serialTool.findAllSerial();
        Iterator<String> it = l.iterator();
        while (it.hasNext()) {
            String str = it.next();
            System.out.println(str);

            final JRadioButtonMenuItem rb = new JRadioButtonMenuItem(str);
            rb.setName(str);
            comButtonGroup.add(rb);
            comMenu.add(rb);
            rb.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println(rb.getName()+": "+rb.isSelected());
                    serialTool.OpenSerialTool(rb.getName());
                }
            });
            // 设置默认项
            if(first_flag) {
                rb.setSelected(true);
                serialTool.OpenSerialTool(rb.getName());
                first_flag = false;
            }
        }
    }
    public ChartPanel(String title, RealDataSet realDataSet, SerialTool serialTool, String version) {

        super(title);
        super.setDefaultCloseOperation(EXIT_ON_CLOSE);
        // 设置串口
        JMenuBar menuBar = new JMenuBar();
        comMenu = new JMenu("串口");
        menuBar.add(comMenu);
        flushMenuItem = new JMenuItem("刷新");
        flashMenu(serialTool);
        flushMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                flashMenu(serialTool);
            }
        });
        super.setJMenuBar(menuBar);
        // 设置版本号
        JMenu aboutMenu = new JMenu("关于");
        JMenuItem versionMenuItem = new JMenuItem("V "+version);
        aboutMenu.add(versionMenuItem);
        menuBar.add(aboutMenu);
        // 时域数据集设置
        final XYSeriesCollection originDataSet = new XYSeriesCollection();
        originDataSet.addSeries(realDataSet.getOriginSeries());
        // 频域数据集设置
        final XYSeriesCollection fftDataSet = new XYSeriesCollection();
        fftDataSet.addSeries(realDataSet.getFftSeries());
        // 时域图形设置
        JFreeChart originChart = ChartFactory.createXYLineChart(null, "Time", "blood oxygen pleth", originDataSet,
                PlotOrientation.VERTICAL, false, true, false);
//        originChart.setBorderPaint(Color.black);
//        originChart.setBorderVisible(true);
//        originChart.setBackgroundPaint(Color.white);
        final XYPlot plot = originChart.getXYPlot();
//        plot.setBackgroundPaint(Color.lightGray);
//        plot.setDomainGridlinePaint(Color.white);
//        plot.setRangeGridlinePaint(Color.white);
        final XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setDefaultShapesVisible(false);
        renderer.setSeriesPaint(0, Color.black);
        final ValueAxis axis = plot.getDomainAxis();
        axis.setAutoRange(true);
        axis.setFixedAutoRange(1024);
        // 频谱图设置
        JFreeChart fftChart = ChartFactory.createXYLineChart(null, "Hz", " ", fftDataSet,
                PlotOrientation.VERTICAL, false, true, false);
        final XYPlot fftPlot = fftChart.getXYPlot();
        final XYLineAndShapeRenderer fftRenderer = (XYLineAndShapeRenderer) fftPlot.getRenderer();
        fftRenderer.setDefaultShapesVisible(false);
        fftRenderer.setSeriesPaint(0,Color.RED);
        final ValueAxis fftAxis = fftPlot.getDomainAxis();
        fftAxis.setRange(new Range(0,10));

        // 界面设置
        final JPanel content = new JPanel(new GridLayout(3,1));
        final org.jfree.chart.ChartPanel chartPanel = new org.jfree.chart.ChartPanel(originChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(900, 320));
        chartPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        content.add(chartPanel);

        final org.jfree.chart.ChartPanel fftPanel = new org.jfree.chart.ChartPanel(fftChart);
        fftPanel.setPreferredSize(new java.awt.Dimension(900, 320));
        fftPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        content.add(fftPanel);

        final JPanel infoLabel = new JPanel();
        infoLabel.add(realDataSet.getJ1());
        content.add(infoLabel);

        setContentPane(content);
    }

}