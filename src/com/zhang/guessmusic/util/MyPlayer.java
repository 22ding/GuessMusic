package com.zhang.guessmusic.util;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

/**
 * ���ֲ�����
 * 
 * @author ZhangJY
 *
 */
public class MyPlayer {

	//��������
	private static MediaPlayer mMusicMediaPlayer;
	
	/**
	 * ���Ÿ���
	 * 
	 * @param context
	 * @param fileName
	 */
	public static void playSong(Context context, String fileName) {
		if (null == mMusicMediaPlayer) {
			mMusicMediaPlayer = new MediaPlayer();
		}
		
		//ǿ�����ò���״̬
		mMusicMediaPlayer.reset();
		
		//���������ļ�
		AssetManager assertManager = context.getAssets();
		try {
			AssetFileDescriptor fileDescriptor = assertManager.openFd(fileName);
			mMusicMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), 
											fileDescriptor.getStartOffset(), 
											fileDescriptor.getLength());
			mMusicMediaPlayer.prepare();
			mMusicMediaPlayer.start();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void stopSong(Context context) {
		if (mMusicMediaPlayer != null) {
			mMusicMediaPlayer.stop();
		}
	}
}
