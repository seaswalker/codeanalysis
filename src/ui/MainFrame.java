package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import service.Service;
import util.FrameUtil;

import javax.swing.JCheckBox;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = -1958905315494234604L;
	private JPanel contentPane;
	private JTextField directoryPath;
	//统计服务
	private Service service = new Service();
	//选中的路径
	private File directory;
	//显示结果区域
	private JTextPane resultPane = null;
	//文件选择器
	private JFileChooser chooser = null;
	//统计结果
	private String statisticsResult;
	private JButton saveButton = null;
	private JButton startButton = null;
	//语言控件数组
	private JCheckBox[] checkBoxes = new JCheckBox[3];
	private boolean isStatictisChild = true;
	//语言选择结果
	private List<String> languages = new ArrayList<String>(3);
	//标记是否有选中项(语言)
	private boolean isSelected = false;
	//语言扩展名数组
	private static String[] languagesArray = new String[] {"java", "C", "javascript"};
 
	/**
	 * Create the frame.
	 */
	public MainFrame() {
		//Windows样式
		try {
			//windows样式					 
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		setResizable(false);
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainFrame.class.getResource("/icon.png")));
		setTitle("\u4EE3\u7801\u7EDF\u8BA1");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//窗体居中
		Point point = FrameUtil.getMiddlePoint(300, 550);
		setBounds(point.x, point.y, 300, 550);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel label = new JLabel("\u8BF7\u9009\u62E9\u60A8\u8981\u7EDF\u8BA1\u7684\u76EE\u5F55\uFF1A");
		label.setFont(new Font("宋体", Font.PLAIN, 14));
		label.setBounds(10, 10, 173, 22);
		contentPane.add(label);
		
		directoryPath = new JTextField();
		directoryPath.setFont(new Font("宋体", Font.PLAIN, 12));
		directoryPath.setEditable(false);
		directoryPath.setBounds(10, 42, 173, 22);
		contentPane.add(directoryPath);
		directoryPath.setColumns(10);
		
		//记住上次的路径
		Preferences preferences = Preferences.userRoot().node(this.getClass().getName());
		String lastPath = preferences.get("lastPath", "");
		//文件选择控件
		if(!"".equals(lastPath)) {
			chooser = new JFileChooser(lastPath);
		}else {
			chooser = new JFileChooser();
		}
		chooser.setFileHidingEnabled(false);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle("请选择目录...");
		
		
		JButton chooseButton = new JButton("\u9009\u62E9");
		chooseButton.setFont(new Font("宋体", Font.PLAIN, 12));
		chooseButton.setBounds(195, 41, 79, 23);
		chooseButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int returnal = chooser.showOpenDialog(contentPane);
				if(returnal == JFileChooser.APPROVE_OPTION) {
					File selectedFile = chooser.getSelectedFile();
					String path = selectedFile.getAbsolutePath();
					//设置路径显示控件
					directoryPath.setText(path);
					directory = selectedFile;
					//设置上次路径
					preferences.put("lastPath", selectedFile.getAbsolutePath());
					//统计可用
					startButton.setEnabled(true);
				}
			}
		});
		contentPane.add(chooseButton);
		
		startButton = new JButton("\u7EDF\u8BA1");
		startButton.setFont(new Font("宋体", Font.PLAIN, 12));
		startButton.setBounds(93, 191, 93, 23);
		//选择目录之前不可用
		startButton.setEnabled(false);
		startButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					//检查选择的语言
					for(int i = 0;i < checkBoxes.length;i ++) {
						if(checkBoxes[i].isSelected()) {
							languages.add(languagesArray[i]);
							isSelected = true;
						}
					}
					if(!isSelected) {
						JOptionPane.showMessageDialog(contentPane, "请选择语言！", "error:", JOptionPane.ERROR_MESSAGE);
						return;
					}
					//统计
					String result = service.start(directory, languages, isStatictisChild);
					//重新初始化语言和选择标志位
					languages.clear();
					isSelected = false;
					resultPane.setText(result);
					statisticsResult = result;
					//保存可用
					saveButton.setEnabled(true);
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(contentPane, e1.getMessage(), "提示", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		contentPane.add(startButton);
		
		JLabel label_1 = new JLabel("\u7EDF\u8BA1\u7ED3\u679C\uFF1A");
		label_1.setFont(new Font("宋体", Font.PLAIN, 14));
		label_1.setBounds(10, 224, 173, 22);
		contentPane.add(label_1);
		JScrollPane jScrollPane = new JScrollPane();
		jScrollPane.getViewport().setBackground(Color.WHITE);
		jScrollPane.setBounds(10, 256, 264, 222);
		//水平滚动条自动出现
		jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		//垂直滚动条自动出现
		jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jScrollPane.setVisible(true);
		contentPane.add(jScrollPane);
		
		resultPane = new JTextPane();
		jScrollPane.setViewportView(resultPane);
		resultPane.setEditable(false);
		resultPane.setFont(new Font("宋体", Font.PLAIN, 14));
		
		saveButton = new JButton("\u4FDD\u5B58");
		saveButton.setFont(new Font("宋体", Font.PLAIN, 12));
		saveButton.setBounds(38, 488, 93, 23);
		//未统计之前不可用
		saveButton.setEnabled(false);
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(service.saveResult(directory, statisticsResult)) {
					JOptionPane.showMessageDialog(contentPane, "结果已保存至桌面", "success", JOptionPane.INFORMATION_MESSAGE);
				}else {
					JOptionPane.showMessageDialog(contentPane, "保存失败", "error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		contentPane.add(saveButton);
		
		JButton exitButton = new JButton("\u9000\u51FA");
		exitButton.setFont(new Font("宋体", Font.PLAIN, 12));
		exitButton.setBounds(163, 488, 93, 23);
		exitButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		contentPane.add(exitButton);
		
		JLabel label_2 = new JLabel("请选择您要统计的语言:");
		label_2.setFont(new Font("宋体", Font.PLAIN, 14));
		label_2.setBounds(10, 74, 173, 22);
		contentPane.add(label_2);
		
		JCheckBox javaBox = new JCheckBox("java");
		javaBox.setFont(new Font("宋体", Font.PLAIN, 12));
		javaBox.setBounds(20, 103, 70, 23);
		contentPane.add(javaBox);
		//加入数组
		checkBoxes[0] = javaBox;
		
		JCheckBox cBox = new JCheckBox("C语言");
		cBox.setFont(new Font("宋体", Font.PLAIN, 12));
		cBox.setBounds(93, 103, 70, 23);
		contentPane.add(cBox);
		//加入数组
		checkBoxes[1] = cBox;
		
		JCheckBox javascriptBox = new JCheckBox("javascript");
		javascriptBox.setFont(new Font("宋体", Font.PLAIN, 12));
		javascriptBox.setBounds(178, 102, 96, 23);
		contentPane.add(javascriptBox);
		//加入数组
		checkBoxes[2] = javascriptBox;
		
		//是否统计子包
		JLabel childLabel = new JLabel("是否统计子文件夹?");
		childLabel.setFont(new Font("宋体", Font.PLAIN, 14));
		childLabel.setBounds(10, 134, 140, 22);
		contentPane.add(childLabel);
		
		ButtonGroup group = new ButtonGroup();
		JRadioButton yes = new JRadioButton("是");
		yes.setSelected(true);
		yes.setFont(new Font("宋体", Font.PLAIN, 12));
		yes.setBounds(20, 162, 103, 23);
		yes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				isStatictisChild = true;
			}
		});
		group.add(yes);
		contentPane.add(yes);
		
		JRadioButton no = new JRadioButton("否");
		no.setFont(new Font("宋体", Font.PLAIN, 12));
		no.setBounds(125, 162, 103, 23);
		no.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				isStatictisChild = false;
			}
		});
		group.add(no);
		contentPane.add(no);
	}
}
