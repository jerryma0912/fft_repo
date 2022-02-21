package cn.hyperchain.business;

import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.junit.Test;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.KeyEvent;

import static javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS;
import static javax.swing.SwingConstants.CENTER;

@Slf4j
public class Panel extends JFrame{

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int width = screenSize.width;
    int height = screenSize.height;

    public Panel() {
        super("脉搏监测工具");
//        super.setUndecorated(true);
        super.setDefaultCloseOperation(EXIT_ON_CLOSE);
        super.setExtendedState(Frame.MAXIMIZED_BOTH);
        log.info("screen size is :" + width + " x "+ height);
        setJMenuBar(setMenu());
        setUpper();
        setCenter();
        setBottom();

        super.pack();
        super.setVisible(true);
//        super.setResizable(false);
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
        return menuBar;
    }

    private void setUpper() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new CompoundBorder(new LineBorder(Color.GRAY),new EmptyBorder(5, 5, 5, 5)));
        JPanel panel = new JPanel(new GridLayout(1,2));
//        JLabel label = new JLabel("Create a google account.");
//        label.setHorizontalAlignment(JLabel.CENTER);
//        label.setFont(label.getFont().deriveFont(25f));
//        panel.add(label);
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton but1 = new JButton("保存诊断记录");
        JButton but2 = new JButton("清空诊断记录");
        leftPanel.add(but1);
        leftPanel.add(but2);
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton but5 = new JButton("暂停");
        JButton but6 = new JButton("启动");
        JButton but3 = new JButton("保存脉搏波形");
        JButton but4 = new JButton("清除选中波形");
        rightPanel.add(but5);
        rightPanel.add(but6);
        rightPanel.add(but3);
        rightPanel.add(but4);

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
        JPanel personalPanel=new JPanel(new GridLayout(1, 4));
        personalPanel.setBorder(new TitledBorder(new CompoundBorder(new LineBorder(Color.GRAY), new EmptyBorder(1, 1, 1, 1)), "个人信息"));
        personalPanel.setBackground(Color.WHITE);
        createInputPanel(personalPanel);
        createInputPanel(personalPanel);
        createInputPanel(personalPanel);
        createInputPanel(personalPanel);
        JComponent panel1 = makeDiagPanel();
        p.add(personalPanel,BorderLayout.NORTH);
        p.add(panel1,BorderLayout.CENTER);

        tabbedPane.addTab("患者信息",null, p,"输入诊断信息");
//        tabbedPane.addTab("已保存监测信息",null, panel2,"查看脉搏监测信息");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        fatherPanel.add(tabbedPane);
    }

    protected JComponent makeDiagPanel()
    {
        JPanel panel = new JPanel(new GridLayout(3, 1));
        // 2
        JPanel diagPanel=new JPanel(new GridLayout(1, 1));
        diagPanel.setBorder(new TitledBorder(new CompoundBorder(new LineBorder(Color.GRAY), new EmptyBorder(1, 1, 1, 1)), "主诉症状"));
        diagPanel.setBackground(Color.WHITE);
        JTextArea jta=new JTextArea("请输入内容");
        jta.setLineWrap(true);    //设置文本域中的文本为自动换行
        jta.setForeground(Color.BLACK);    //设置组件的背景色
        JScrollPane jsp=new JScrollPane(jta);    //将文本域放入滚动窗口
        jsp.setBorder(new CompoundBorder(new EmptyBorder(5,5,5,5), new LineBorder(Color.lightGray)));
        jsp.setBackground(Color.WHITE);
//        Dimension size=jta.getPreferredSize();    //获得文本域的首选大小
//        jsp.setBounds(110,90,size.width,size.height);
        diagPanel.add(jsp);

        // 3
        JPanel diagPanel2=new JPanel(new GridLayout(1, 1));
        diagPanel2.setBorder(new TitledBorder(new CompoundBorder(new LineBorder(Color.GRAY), new EmptyBorder(1, 1, 1, 1)), "诊断结果"));
        diagPanel2.setBackground(Color.WHITE);
        JTextArea jta2=new JTextArea("请输入内容");
        jta2.setLineWrap(true);    //设置文本域中的文本为自动换行
        jta2.setForeground(Color.BLACK);    //设置组件的背景色
        JScrollPane jsp2=new JScrollPane(jta2);    //将文本域放入滚动窗口
        jsp2.setBorder(new CompoundBorder(new EmptyBorder(5,5,5,5), new LineBorder(Color.lightGray)));
        jsp2.setBackground(Color.WHITE);
//        Dimension size=jta.getPreferredSize();    //获得文本域的首选大小
//        jsp.setBounds(110,90,size.width,size.height);
        diagPanel2.add(jsp2);

        // 4
        JPanel addPanel=new JPanel(new GridLayout(1, 1));
        addPanel.setBorder(new TitledBorder(new CompoundBorder(new LineBorder(Color.GRAY), new EmptyBorder(1, 1, 1, 1)), "附加信息"));
        addPanel.setBackground(Color.WHITE);
        JList list=new JList();
        list.setBorder(new CompoundBorder(new EmptyBorder(5,5,5,5), new LineBorder(Color.lightGray)));
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(list);
        String[] listData=new String[7];
        listData[0]="20220220103822";
        listData[1]="20220220103823";
        listData[2]="20220220103824";
        listData[3]="20220220103825";
        listData[4]="20220220103826";
        listData[5]="20220220103827";
        listData[6]="20220220103828";
        list.setListData(listData);
        addPanel.add(list);

//        JLabel filler = new JLabel("test");
//        panel.setBackground(Color.WHITE);
//        filler.setHorizontalAlignment(CENTER);
//        panel.setLayout(new GridLayout(1,1));
//        panel.add(filler);
//        panel.add(personalPanel);
        panel.add(diagPanel);
        panel.add(diagPanel2);
        panel.add(addPanel);

        return panel;
    }

    private void createInputPanel(JPanel father) {
        JPanel namePanel = new JPanel(new GridLayout(1, 2));
        namePanel.setBorder(new EmptyBorder(1,2,1,2));
        JLabel label = new JLabel("姓名");
        JTextField val = new JTextField(5);
        namePanel.add(label);
        namePanel.add(val);
        father.add(namePanel);
    }

    private void setRight(JPanel fatherPanel) {
//        JPanel rightBoxedPanel = new JPanel();
//        rightBoxedPanel.setLayout(new BoxLayout(rightBoxedPanel, BoxLayout.Y_AXIS));
//        rightBoxedPanel.setBorder(new TitledBorder(new CompoundBorder(new LineBorder(Color.GRAY), new EmptyBorder(1, 1, 1, 1)), "脉搏监测"));
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(new EmptyBorder(0,2,0,2));
        JComponent panel1 = makePicPanel();
        JComponent panel2 = makePicPanel();
        tabbedPane.addTab("实时监测信息",null, panel1,"查看实时脉搏监测信息");
        tabbedPane.addTab("历史监测信息",null, panel2,"查看历史脉搏监测信息");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        fatherPanel.add(tabbedPane);
    }

    protected JComponent makePicPanel()
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setLayout(new GridLayout(3,1));
        panel.setBackground(Color.WHITE);
        // 时域波形图
        XYDataset originDataSet = null;
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
        XYDataset fftDataSet = null;
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

        // 表格
        String[] name={"基波","点位索引","频率(Hz)","幅值(%)","相位(°)","实际值"};
        Object[][] tableDate=new Object[10][6];
        for(int i=0; i<10; i++) {
            for(int j=0;j<6;j++) {
                tableDate[i][j] = i+j;
            }
        }
        JTable table=new JTable(tableDate,name){
            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                TableCellRenderer renderer = super.getCellRenderer(row, column);
                if (renderer instanceof JLabel) {
                    ((JLabel) renderer).setHorizontalAlignment(JLabel.CENTER);
                }
                return renderer;
            }
        };
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
//        bottomPanel.setBorder(new TitledBorder("bottomPanel - GridLayout"));


        JPanel statusBar = new JPanel(new GridLayout(1, 3));
//        statusBar.setBorder(BorderFactory.createCompoundBorder(raisedBevelBorder, loweredBevelBorder));

        addBottomLabel(statusBar);
        addBottomLabel(statusBar);
        addBottomLabel(statusBar);
        addBottomLabel(statusBar);
        addBottomLabel(statusBar);

        getContentPane().add(statusBar,BorderLayout.SOUTH);
    }

    private void addBottomLabel(JPanel father) {
        Border loweredBevelBorder = BorderFactory.createLoweredBevelBorder();
        Border raisedBevelBorder = BorderFactory.createRaisedBevelBorder();

        JPanel stPanel = new JPanel(new GridLayout(1, 2));
        stPanel.setBorder(BorderFactory.createCompoundBorder(raisedBevelBorder, loweredBevelBorder));
        JLabel label = new JLabel("状态1:");
        label.setFont(label.getFont().deriveFont(10f));
        label.setHorizontalAlignment(CENTER);
        JLabel status = new JLabel("正常");
        status.setFont(status.getFont().deriveFont(10f));
        stPanel.add(label);
        stPanel.add(status);
        father.add(stPanel);
    }



}
