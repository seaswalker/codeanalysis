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
 * ����ͳ���߼�
 * @author skywalker
 *
 */
public class Service {
	
	//��ʵ���������Ե�ע�Ͷ���һ����
	private static Pattern notes = Pattern.compile("^(\\W)*((/\\*)|(\\*)|(\\*/)|(//))");
	private static Pattern spaces = Pattern.compile("^\\s*$");
	private Matcher matcher = null;
	private BufferedReader br = null;
	private StatisticsResult statisticsResult = null;
	//��¼���������������ܺ�
	private int notesCount = 0;
	private int spacesCount = 0;
	private int codesCount = 0;
	private int total = 0;
	private int filesCount = 0;
	private int totalCount = 0;
	
	/**
	 * ͳ��
	 */
	public String start(File directory, List<String> languages, boolean isStatictisChild) throws IOException {
		reset();
		//�����������͹�����map
		Map<String, StatisticsResult> map = new LinkedHashMap<String, StatisticsResult>(languages.size());
		String language = null;
		for(int i = 0;i < languages.size();i ++) {
			language = languages.get(i);
			map.put(language, new StatisticsResult(language));
		}
		//��ʼ�ݹ�
		recursion(directory, map, isStatictisChild);
		//���ɽ���ַ���
		StringBuilder sb = new StringBuilder("ͳ�ƽ�����£�\r\n");
		int size = map.size();
		for(Entry<String, StatisticsResult> entry : map.entrySet()) {
			sb.append("  ��ͳ��").append(entry.getKey()).append("Դ�ļ�");
			statisticsResult = entry.getValue();
			totalCount = statisticsResult.getTotal();
			if(size > 1) {
				notesCount += statisticsResult.getNotesCount();
				spacesCount += statisticsResult.getSpacesCount();
				codesCount += statisticsResult.getCodesCount();
				total += totalCount;
				filesCount += statisticsResult.getFilesCount();
			}
			sb.append(statisticsResult.getFilesCount()).append("��:\r\n").append("    ע��").append(statisticsResult.getNotesCount()).append("��\r\n")
				.append("    ����").append(statisticsResult.getSpacesCount()).append("��\r\n")
				.append("    ����").append(statisticsResult.getCodesCount()).append("��\r\n")
				.append("    �ܼ�").append(totalCount).append("��\r\n");
		}
		//��ͳ�ƴ���һ������ʱ����ʾ�ϼ���Ϣ
		if(size > 1) {
			sb.append("�ϼƣ�\r\n").append("  ��ͳ��").append(filesCount).append("���ļ�\r\n")
				.append("    ע��").append(notesCount).append("��\r\n")
				.append("    ����").append(spacesCount).append("��\r\n")
				.append("    ����").append(codesCount).append("��\r\n")
				.append("    �ܼ�").append(total).append("��\r\n");
		}
		return sb.toString();
	}
	
	/**
	 * ��ʼ��������
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
	 * ��ʼ�ݹ����
	 */
	private void recursion(File directory, Map<String, StatisticsResult> map, boolean isStatictisChild) throws IOException {
		if(!directory.exists()) {
			throw new FileNotFoundException("�ļ�δ�ҵ�");
		}
		File[] files = directory.listFiles();
		for(File file : files) {
			if(file.isDirectory()) {
				if(isStatictisChild) {
					//��Ŀ¼���ݹ�
					recursion(file, map, true);
				}
			}else {
				statistics(file, map);
			}
		}
	}
	
	/**
	 * ����һ���ļ�
	 */
	private void statistics(File file, Map<String, StatisticsResult> map) throws IOException {
		//��ȡ��չ��
		String extendsion = getExtension(file);
		statisticsResult = map.get(extendsion);
		//�������Ҫͳ�ƴ�������ֱ������
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
	 * ������չ��������������
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
	 * �ѽ������������
	 */
	public boolean saveResult(File directory, String result) {
		File file = new File("C:/Users/xsdwe_000/Desktop/result.txt");
		//ɾ�������ļ�
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
