package com.zhang.guessmusic.util;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

/**
 * 音乐播放类
 * 
 * @author ZhangJY
 *
 */
public class MyPlayer {

	//歌曲播放
	private static MediaPlayer mMusicMediaPlayer;
	
	/**
	 * 播放歌曲
	 * 
	 * @param context
	 * @param fileName
	 */
	public static void playSong(Context context, String fileName) {
		if (null == mMusicMediaPlayer) {
			mMusicMediaPlayer = new MediaPlayer();
		}
		
		//强制重置播放状态
		mMusicMediaPlayer.reset();
		
		//加载声音文件
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
