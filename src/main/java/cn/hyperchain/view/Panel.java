package cn.hyperchain.view;

import cn.hyperchain.business.SerialTool;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.SwingConstants.CENTER;

@Slf4j
public class Panel extends JFrame{

    // ---------DATA----------
//    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//    int width = screenSize.width;
//    int height = screenSize.height;
    private SerialTool serialTool = null;
    private JMenu comMenu = null;
    private JMenuItem flushMenuItem = null;

    private final String[] name = {"基波","点位索引","频率(Hz)","幅值(%)","相位(°)","实际值"};

    private final JButton saveButton = new JButton("保存诊断记录");
    private final JButton clearButton = new JButton("清空诊断记录");
    private final JButton pauseButton = new JButton("暂停");
    private final JButton startButton = new JButton("启动");
    private final JButton saveWaveButton = new JButton("保存脉搏波形");
    private final JButton clearWaveButton = new JButton("清除选中波形");

    private final JTextField nameTextField = new JTextField(5);
    private final JComboBox sexComboBox = new JComboBox();
    private final JTextField ageTextField = new JTextField(5);
    private final JTextField telTextField = new JTextField(10);
    private final JTextArea complaintJta = new JTextArea("请输入内容");
    private final JTextArea diagJta = new JTextArea("请输入内容");
    private final JList odditionalList = new JList();
    private final ArrayList<String> listData = new ArrayList();

    private final XYSeries realOriginSeries = new XYSeries("realOrigin");
    private final XYSeries realFftSeries = new XYSeries("realFft");
    private final Object[][] realTableDate = new Object[10][6];
    private final JTable realTable = new JTable(realTableDate,name){
        @Override
        public TableCellRenderer getCellRenderer(int row, int column) {
            TableCellRenderer renderer = super.getCellRenderer(row, column);
            if (renderer instanceof JLabel) {
                ((JLabel) renderer).setHorizontalAlignment(JLabel.CENTER);
            }
            return renderer;
        }
    };

    private final XYSeries hisOriginSeries = new XYSeries("hisOrigin");
    private final XYSeries hisFftSeries = new XYSeries("hisFft");
    private final Object[][] hisTableDate = new Object[10][6];
    private final JTable hisTable = new JTable(hisTableDate,name){
        @Override
        public TableCellRenderer getCellRenderer(int row, int column) {
            TableCellRenderer renderer = super.getCellRenderer(row, column);
            if (renderer instanceof JLabel) {
                ((JLabel) renderer).setHorizontalAlignment(JLabel.CENTER);
            }
            return renderer;
        }
    };

    private final JLabel serialStatus = new JLabel();
    private final JLabel status2 = new JLabel("正常");
    private final JLabel status3 = new JLabel("正常");
    private final JLabel status4 = new JLabel("正常");
    private final JLabel status5 = new JLabel("正常");



    // ---------ACTION-----------
    private void flashMenu(SerialTool serialTool) {
        boolean first_flag = true;
        // 清除老按钮
        comMenu.removeAll();
        comMenu.add(flushMenuItem);
        comMenu.addSeparator();
        // 添加新按钮
        ButtonGroup comButtonGroup = new ButtonGroup();
        List<String> l = serialTool.findAllSerial();
        for (String str : l) {
            final JRadioButtonMenuItem rb = new JRadioButtonMenuItem(str);
            rb.setName(str);
            comButtonGroup.add(rb);
            comMenu.add(rb);
            rb.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println(rb.getName() + ": " + rb.isSelected());
                    serialTool.OpenSerialTool(rb.getName());

                    serialStatus.setText(rb.getText());
                }
            });
            // 设置默认项
            if (first_flag) {
                rb.setSelected(true);
                serialTool.OpenSerialTool(rb.getName());
                first_flag = false;

                serialStatus.setText(rb.getText());
            }
        }
    }

    private class saveButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
//            String s3 = "1";
//            listData.add(s3);
//            odditionalList.setListData(listData.toArray());

            Integer p = Integer.parseInt(diagJta.getText());
            for (int i=0; i<10; i++) {
                for(int j=0;j<6;j++) {
                    realTableDate[i][j] = i+j+p;
                }
            }
            realTable.updateUI();
        }
    }


    // ---------------VIEW------------------
    public Panel(SerialTool s) {
        super("脉搏监测工具");
//        super.setUndecorated(true);   // 取消状态栏
        super.setDefaultCloseOperation(EXIT_ON_CLOSE);
        super.setExtendedState(Frame.MAXIMIZED_BOTH);

        serialTool = s;

        setJMenuBar(setMenu());
        setUpper();
        setCenter();
        setBottom();

        super.pack();
        super.setVisible(true);
        super.setResizable(false);

        saveButton.addActionListener(new saveButtonHandler());
    }

    private JMenuBar setMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("文件");
        JMenuItem newMenuItem = new JMenuItem("新建..");
        JMenuItem openMenuItem = new JMenuItem("打开..");
        JMenuItem exitMenuItem = new JMenuItem("退出");
        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);

        JMenu editMenu = new JMenu("设置");
        JMenu showExamMenu = new JMenu("监测显示模式");
        ButtonGroup comButtonGroup = new ButtonGroup();
        JRadioButtonMenuItem examDetailItem = new JRadioButtonMenuItem("详细模式");
        JRadioButtonMenuItem examSimpItem = new JRadioButtonMenuItem("精简模式");
        comButtonGroup.add(examDetailItem);
        comButtonGroup.add(examSimpItem);
        examDetailItem.setSelected(true); // 设置默认
        showExamMenu.add(examDetailItem);
        showExamMenu.add(examSimpItem);
        editMenu.add(showExamMenu);
        menuBar.add(editMenu);

        comMenu = new JMenu("串口");
        flushMenuItem = new JMenuItem("刷新");
        flashMenu(serialTool);
        flushMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                flashMenu(serialTool);
            }
        });
        menuBar.add(comMenu);

        return menuBar;
    }

    private void setUpper() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new CompoundBorder(new LineBorder(Color.GRAY),new EmptyBorder(5, 5, 5, 5)));
        JPanel panel = new JPanel(new GridLayout(1,2));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(saveButton);
        leftPanel.add(clearButton);
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rightPanel.add(pauseButton);
        rightPanel.add(startButton);
        rightPanel.add(saveWaveButton);
        rightPanel.add(clearWaveButton);

        panel.add(leftPanel);
        panel.add(rightPanel);
        topPanel.add(panel, BorderLayout.PAGE_START);
        setContentPane(topPanel);
    }

    private void setCenter() {
        JPanel gridPanel = new JPanel(new GridLayout(1, 2));
//        gridPanel.setBorder(new TitledBorder("gridPanel - GridLayout"));
        getContentPane().add(gridPanel, BorderLayout.CENTER);
        setLeft(gridPanel);
        setRight(gridPanel);
    }

    private void setLeft(JPanel fatherPanel) {
//        JPanel leftBoxedPanel = new JPanel();
//        leftBoxedPanel.setLayout(new BoxLayout(leftBoxedPanel, BoxLayout.Y_AXIS));
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(new EmptyBorder(0,2,0,2));
        //ImageIcon icon=createImageIcon("tab.jp1g");

        JPanel p = new JPanel(new BorderLayout());
        JPanel personalPanel = new JPanel(new GridLayout(1, 4));
        personalPanel.setBorder(new TitledBorder(new CompoundBorder(new LineBorder(Color.GRAY), new EmptyBorder(1, 1, 1, 1)), "个人信息"));
        personalPanel.setBackground(Color.WHITE);

        // 姓名
        JPanel namePanel = new JPanel(new GridLayout(1, 2));
        namePanel.setBorder(new EmptyBorder(1,2,1,2));
        namePanel.setBackground(Color.WHITE);
        JLabel label = new JLabel("姓名 : ",JLabel.CENTER);
        namePanel.add(label);
        namePanel.add(nameTextField);
        personalPanel.add(namePanel);
        // 性别
        JPanel sexPanel = new JPanel(new GridLayout(1, 2));
        sexPanel.setBorder(new EmptyBorder(1,2,1,2));
        sexPanel.setBackground(Color.WHITE);
        JLabel sexlabel = new JLabel("性别 : ", CENTER);
        sexComboBox.setBackground(Color.white);
        sexComboBox.addItem("男");    //向下拉列表中添加一项
        sexComboBox.addItem("女");
        sexPanel.add(sexlabel);
        sexPanel.add(sexComboBox);
        personalPanel.add(sexPanel);
        // 年龄
        JPanel agePanel = new JPanel(new GridLayout(1, 2));
        agePanel.setBorder(new EmptyBorder(1,2,1,2));
        agePanel.setBackground(Color.WHITE);
        JLabel agelabel = new JLabel("年龄 : ",JLabel.CENTER);
        agePanel.add(agelabel);
        agePanel.add(ageTextField);
        personalPanel.add(agePanel);
        // 电话
        JPanel telPanel = new JPanel(new GridLayout(1, 2));
        telPanel.setBorder(new EmptyBorder(1,2,1,2));
        telPanel.setBackground(Color.WHITE);
        JLabel tellabel = new JLabel("电话 : ",JLabel.CENTER);
        telPanel.add(tellabel);
        telPanel.add(telTextField);
        personalPanel.add(telPanel);

        JComponent panel1 = makeDiagPanel();
        p.add(personalPanel,BorderLayout.NORTH);
        p.add(panel1,BorderLayout.CENTER);

        tabbedPane.addTab("患者信息",null, p,"输入诊断信息");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        fatherPanel.add(tabbedPane);
    }

    protected JComponent makeDiagPanel()
    {
        JPanel panel = new JPanel(new GridLayout(3, 1));
        // 主诉症状
        JPanel diagPanel=new JPanel(new GridLayout(1, 1));
        diagPanel.setBorder(new TitledBorder(new CompoundBorder(new LineBorder(Color.GRAY), new EmptyBorder(1, 1, 1, 1)), "主诉症状"));
        diagPanel.setBackground(Color.WHITE);
        complaintJta.setLineWrap(true);    //设置文本域中的文本为自动换行
        complaintJta.setForeground(Color.BLACK);    //设置组件的背景色
        JScrollPane jsp=new JScrollPane(complaintJta);    //将文本域放入滚动窗口
        jsp.setBorder(new CompoundBorder(new EmptyBorder(5,5,5,5), new LineBorder(Color.lightGray)));
        jsp.setBackground(Color.WHITE);
//        Dimension size=jta.getPreferredSize();    //获得文本域的首选大小
//        jsp.setBounds(110,90,size.width,size.height);
        diagPanel.add(jsp);

        // 诊断结果
        JPanel diagPanel2=new JPanel(new GridLayout(1, 1));
        diagPanel2.setBorder(new TitledBorder(new CompoundBorder(new LineBorder(Color.GRAY), new EmptyBorder(1, 1, 1, 1)), "诊断结果"));
        diagPanel2.setBackground(Color.WHITE);
        diagJta.setLineWrap(true);    //设置文本域中的文本为自动换行
        diagJta.setForeground(Color.BLACK);    //设置组件的背景色
        JScrollPane jsp2=new JScrollPane(diagJta);    //将文本域放入滚动窗口
        jsp2.setBorder(new CompoundBorder(new EmptyBorder(5,5,5,5), new LineBorder(Color.lightGray)));
        jsp2.setBackground(Color.WHITE);
        diagPanel2.add(jsp2);

        // 附加信息
        JPanel addPanel=new JPanel(new GridLayout(1, 1));
        addPanel.setBorder(new TitledBorder(new CompoundBorder(new LineBorder(Color.GRAY), new EmptyBorder(1, 1, 1, 1)), "附加信息"));
        addPanel.setBackground(Color.WHITE);
        odditionalList.setBorder(new CompoundBorder(new EmptyBorder(5,5,5,5), new LineBorder(Color.lightGray)));
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(odditionalList);
        String s1 = "1";
        listData.add(s1);
        String s2 = "1";
        listData.add(s2);
        String s3 = "1";
        listData.add(s3);
        odditionalList.setListData(listData.toArray());
        addPanel.add(scrollPane);

        panel.add(diagPanel);
        panel.add(diagPanel2);
        panel.add(addPanel);

        return panel;
    }

    private void setRight(JPanel fatherPanel) {
//        JPanel rightBoxedPanel = new JPanel();
//        rightBoxedPanel.setLayout(new BoxLayout(rightBoxedPanel, BoxLayout.Y_AXIS));
//        rightBoxedPanel.setBorder(new TitledBorder(new CompoundBorder(new LineBorder(Color.GRAY), new EmptyBorder(1, 1, 1, 1)), "脉搏监测"));
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(new EmptyBorder(0,2,0,2));
        JComponent panel1 = makePicPanel(realOriginSeries, realFftSeries, realTable, realTableDate);
        JComponent panel2 = makePicPanel(hisOriginSeries, hisFftSeries, hisTable, hisTableDate);
        tabbedPane.addTab("实时监测信息",null, panel1,"查看实时脉搏监测信息");
        tabbedPane.addTab("历史监测信息",null, panel2,"查看历史脉搏监测信息");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        fatherPanel.add(tabbedPane);
    }

    protected JComponent makePicPanel(XYSeries originSeries, XYSeries fftSeries, JTable table, Object[][] tableDate)
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setLayout(new GridLayout(3,1));
        panel.setBackground(Color.WHITE);
        // 时域波形图
        final XYSeriesCollection originDataSet = new XYSeriesCollection();
        originDataSet.addSeries(originSeries);
        JFreeChart originChart = ChartFactory.createXYLineChart(null, "Time", "blood oxygen pleth", originDataSet,
                PlotOrientation.VERTICAL, false, true, false);
        final XYPlot plot = originChart.getXYPlot();
        final XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setDefaultShapesVisible(false);
        renderer.setSeriesPaint(0, Color.black);
        final ValueAxis axis = plot.getDomainAxis();
        axis.setAutoRange(true);
        axis.setFixedAutoRange(1024);
        final org.jfree.chart.ChartPanel chartPanel = new org.jfree.chart.ChartPanel(originChart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        chartPanel.setBackground(Color.WHITE);
        panel.add(chartPanel);
        // 频域波形图
        final XYSeriesCollection fftDataSet = new XYSeriesCollection();
        fftDataSet.addSeries(fftSeries);
        JFreeChart fftChart = ChartFactory.createXYLineChart(null, "Hz", " ", fftDataSet,
                PlotOrientation.VERTICAL, false, true, false);
        final XYPlot fftPlot = fftChart.getXYPlot();
        final XYLineAndShapeRenderer fftRenderer = (XYLineAndShapeRenderer) fftPlot.getRenderer();
        fftRenderer.setDefaultShapesVisible(false);
        fftRenderer.setSeriesPaint(0,Color.RED);
        final ValueAxis fftAxis = fftPlot.getDomainAxis();
        fftAxis.setRange(new Range(0,10));
        final org.jfree.chart.ChartPanel fftPanel = new org.jfree.chart.ChartPanel(fftChart);
        fftPanel.setPreferredSize(new java.awt.Dimension(900, 320));
        fftPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        fftPanel.setBackground(Color.WHITE);
        panel.add(fftPanel);

//        updateRowHeights(table, panel.getHeight()/3);
        JScrollPane jp = new JScrollPane(table);
        jp.getViewport().setBackground(Color.WHITE);
        panel.add(jp);
        return panel;
    }

    private void updateRowHeights(JTable table, int hight)
    {
        for (int row = 0; row < table.getRowCount(); row++) {
            int rowHeight = table.getRowHeight();
            for (int column = 0; column < table.getColumnCount(); column++) {
                rowHeight = Math.max(rowHeight, hight);
            }
            table.setRowHeight(row, rowHeight);
        }
    }

    private void setBottom() {

        JPanel statusBar = new JPanel(new GridLayout(1, 3));

        addBottomLabel(statusBar,"当前串口：", serialStatus);
        addBottomLabel(statusBar,"状态2", status2);
        addBottomLabel(statusBar,"状态3", status3);
        addBottomLabel(statusBar,"状态4", status4);
        addBottomLabel(statusBar,"状态5", status5);

        getContentPane().add(statusBar,BorderLayout.SOUTH);
    }

    private void addBottomLabel(JPanel father, String labelStr, JLabel status) {
        Border loweredBevelBorder = BorderFactory.createLoweredBevelBorder();
        Border raisedBevelBorder = BorderFactory.createRaisedBevelBorder();

        JPanel stPanel = new JPanel(new GridLayout(1, 2));
        stPanel.setBorder(BorderFactory.createCompoundBorder(raisedBevelBorder, loweredBevelBorder));
        JLabel label = new JLabel(labelStr);
        label.setFont(label.getFont().deriveFont(10f));
        label.setHorizontalAlignment(CENTER);
//        JLabel status = new JLabel("正常");
        status.setFont(status.getFont().deriveFont(10f));
        stPanel.add(label);
        stPanel.add(status);
        father.add(stPanel);
    }



}
