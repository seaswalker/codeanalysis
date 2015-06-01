package service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.StatisticsResult;


/**
 * 代码统计逻辑
 * @author skywalker
 *
 */
public class Service {
	
	//其实这三种语言的注释都是一样的
	private static Pattern notes = Pattern.compile("^(\\W)*((/\\*)|(\\*)|(\\*/)|(//))");
	private static Pattern spaces = Pattern.compile("^\\s*$");
	private Matcher matcher = null;
	private BufferedReader br = null;
	private StatisticsResult statisticsResult = null;
	//记录各种语言行数的总和
	private int notesCount = 0;
	private int spacesCount = 0;
	private int codesCount = 0;
	private int total = 0;
	private int filesCount = 0;
	private int totalCount = 0;
	
	/**
	 * 统计
	 */
	public String start(File directory, List<String> languages, boolean isStatictisChild) throws IOException {
		reset();
		//根据语言类型构造结果map
		Map<String, StatisticsResult> map = new LinkedHashMap<String, StatisticsResult>(languages.size());
		String language = null;
		for(int i = 0;i < languages.size();i ++) {
			language = languages.get(i);
			map.put(language, new StatisticsResult(language));
		}
		//开始递归
		recursion(directory, map, isStatictisChild);
		//生成结果字符串
		StringBuilder sb = new StringBuilder("统计结果如下：\r\n");
		int size = map.size();
		for(Entry<String, StatisticsResult> entry : map.entrySet()) {
			sb.append("  共统计").append(entry.getKey()).append("源文件");
			statisticsResult = entry.getValue();
			totalCount = statisticsResult.getTotal();
			if(size > 1) {
				notesCount += statisticsResult.getNotesCount();
				spacesCount += statisticsResult.getSpacesCount();
				codesCount += statisticsResult.getCodesCount();
				total += totalCount;
				filesCount += statisticsResult.getFilesCount();
			}
			sb.append(statisticsResult.getFilesCount()).append("个:\r\n").append("    注释").append(statisticsResult.getNotesCount()).append("行\r\n")
				.append("    空行").append(statisticsResult.getSpacesCount()).append("行\r\n")
				.append("    代码").append(statisticsResult.getCodesCount()).append("行\r\n")
				.append("    总计").append(totalCount).append("行\r\n");
		}
		//当统计大于一种语言时才显示合计信息
		if(size > 1) {
			sb.append("合计：\r\n").append("  共统计").append(filesCount).append("个文件\r\n")
				.append("    注释").append(notesCount).append("行\r\n")
				.append("    空行").append(spacesCount).append("行\r\n")
				.append("    代码").append(codesCount).append("行\r\n")
				.append("    总计").append(total).append("行\r\n");
		}
		return sb.toString();
	}
	
	/**
	 * 初始化计数器
	 */
	private void reset() {
		notesCount = 0;
		codesCount = 0;
		spacesCount = 0;
		filesCount = 0;
		total = 0;
		totalCount = 0;
	}
	
	/**
	 * 开始递归过程
	 */
	private void recursion(File directory, Map<String, StatisticsResult> map, boolean isStatictisChild) throws IOException {
		if(!directory.exists()) {
			throw new FileNotFoundException("文件未找到");
		}
		File[] files = directory.listFiles();
		for(File file : files) {
			if(file.isDirectory()) {
				if(isStatictisChild) {
					//是目录，递归
					recursion(file, map, true);
				}
			}else {
				statistics(file, map);
			}
		}
	}
	
	/**
	 * 分析一个文件
	 */
	private void statistics(File file, Map<String, StatisticsResult> map) throws IOException {
		//获取扩展名
		String extendsion = getExtension(file);
		statisticsResult = map.get(extendsion);
		//如果不需要统计此种语言直接跳过
		if(statisticsResult == null) {
			return;
		}
		statisticsResult.addFilesCount();
		br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		String temp = null;
		while((temp = br.readLine()) != null) {
			matcher = spaces.matcher(temp);
			if(temp.equals("") || matcher.matches()) {
				statisticsResult.addSpacesCount();
				continue;
			}
			matcher = notes.matcher(temp);
			if(matcher.find()) {
				statisticsResult.addNotesCount();
				continue;
			}
			statisticsResult.addCodesCount();
		}
	}
	
	/**
	 * 根据扩展名返回语言类型
	 */
	private String getExtension(File file) {
		String fileName = file.getName();
		String extendsion =  fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
		String result = "";
		if(extendsion.equals("java")) {
			result = "java";
		}else if(extendsion.equals("c")) {
			result = "C";
		}else if(extendsion.equals("js")) {
			result = "javascript";
		}
		return result;
	}
	
	/**
	 * 把结果保存至桌面
	 */
	public boolean saveResult(File directory, String result) {
		File file = new File("C:/Users/xsdwe_000/Desktop/result.txt");
		//删除现有文件
		if(file.exists()) {
			file.delete();
		}
		BufferedWriter bw = null;
		String str = directory.getAbsolutePath() + ":\r\n" + result;
		try {
			try {
				bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
				bw.write(str);
				bw.flush();
			}finally {
				if(bw != null) {
					bw.close(); 
				}
			}
		} catch (IOException e) {
			return false;
		}
		return true;
	}
}
