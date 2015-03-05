package com.zhang.guessmusic.model;

public class Song {
	//��������
	private String mSongName;
	//�����ļ���
	private String mFileName;
	//����������
	private int mNameLength;
	
	public char[] getNameCharacters(){
		return mSongName.toCharArray();
	}
	
	public String getSongName() {
		return mSongName;
	}
	
	public void setSongName(String songName) {
		this.mSongName = songName;
		
		this.mNameLength = songName.length();
	}
	
	public String getFileName() {
		return mFileName;
	}
	
	public void setFileName(String fileName) {
		this.mFileName = fileName;
	}
	
	public int getNameLength() {
		return mNameLength;
	}
	
	public void setNameLength(int nameLength) {
		this.mNameLength = nameLength;
	}
}
