package model;

import java.io.Serializable;

/**
 * ��װͳ�ƽ��
 * @author skywalker
 *
 */
public class StatisticsResult implements Serializable {

	private static final long serialVersionUID = -6667379753343285382L;
	
	//����
	private String language;
	//ע������
	private int notesCount;
	//��������
	private int spacesCount;
	//��������
	private int codesCount;
	//�ļ�����
	private int filesCount;
	
	public StatisticsResult(String language) {
		this.language = language;
	}
	
	public StatisticsResult() {}
	
	/**
	 * ����������
	 */
	public int getTotal() {
		return this.spacesCount + this.codesCount + this.notesCount;
	}
	
	public void addNotesCount() {
		this.notesCount ++;
	}
	
	public void addSpacesCount() {
		this.spacesCount ++;
	}
	
	public void addCodesCount() {
		this.codesCount ++;
	}
	
	public void addFilesCount() {
		this.filesCount ++;
	}
	
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public int getNotesCount() {
		return notesCount;
	}

	public void setNotesCount(int notesCount) {
		this.notesCount = notesCount;
	}

	public int getSpacesCount() {
		return spacesCount;
	}

	public void setSpacesCount(int spacesCount) {
		this.spacesCount = spacesCount;
	}

	public int getCodesCount() {
		return codesCount;
	}

	public void setCodesCount(int codesCount) {
		this.codesCount = codesCount;
	}
	
	public int getFilesCount() {
		return filesCount;
	}

	public void setFilesCount(int filesCount) {
		this.filesCount = filesCount;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((language == null) ? 0 : language.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StatisticsResult other = (StatisticsResult) obj;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		return true;
	}
	
}
