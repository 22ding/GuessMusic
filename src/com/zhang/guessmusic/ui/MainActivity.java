package com.zhang.guessmusic.ui;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import junit.framework.Protectable;

import com.zhang.guessmusic.R;
import com.zhang.guessmusic.data.Const;
import com.zhang.guessmusic.model.IWordButtonClickListener;
import com.zhang.guessmusic.model.Song;
import com.zhang.guessmusic.model.WordButton;
import com.zhang.guessmusic.myui.MyGridView;
import com.zhang.guessmusic.util.MyLog;
import com.zhang.guessmusic.util.MyPlayer;
import com.zhang.guessmusic.util.Util;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends Activity implements IWordButtonClickListener{

	public static final String TAG = "MainAvtivity";
	
	public static final int STATUS_ANSWER_RIGHT = 1;
	public static final int STATUS_ANSWER_WRONG = 2;
	public static final int STATUS_ANSWER_LACK = 3;
	
	//闪烁次数
	public static final int ANSWER_SPARK_COINTS = 5;
	
	// 唱片相关动画
	private Animation mPanAnim;
	private LinearInterpolator mPanLin;
	
	// 拨杆相关动画
	private Animation mBarInAnim;
	private LinearInterpolator mBarInLin;
	private Animation mBarOutAnim;
	private LinearInterpolator mBarOutLin;
	
	private ImageView mViewPan;
	private ImageView mViewPanBar;
	
	//play按键事件
    private ImageButton mBtnPlayStart;
    
    //flag
    private boolean mIsRunning = false;
    
    //文字框容器
    private ArrayList<WordButton> mAllWords;
    private ArrayList<WordButton> mSelectWords;
    private MyGridView mMyGridView;
    
    //已选择文字框UI容器
    private LinearLayout mViewWordsContainer;
    
    //当前的歌曲
    private Song mCurrentSong;
    
    //当前关的索引
    private int mCurrentIndex = -1;
    
    //过关界面
    private View mPassView;
    
    //当前金币数量
    private int mCurrentCoins = Const.TOTAL_COINS;
    
    //金币VIEW
    private TextView mViewCurrentCoins;
    
    //当前关索引
    private TextView mCurrentStagePassView;
    private TextView mCurrentStageView;
    
    //当前关歌曲名称
    private TextView mCurrentSongNamePassView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //初始化动画
        mPanAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        mPanLin = new LinearInterpolator();
        mPanAnim.setInterpolator(mPanLin);
        mPanAnim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				mViewPanBar.startAnimation(mBarOutAnim);
			}
		});
        
        mBarInAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_45);
        mBarInAnim.setFillAfter(true);
        mBarInLin = new LinearInterpolator();
        mBarInAnim.setInterpolator(mBarInLin);
        mBarInAnim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				mViewPan.startAnimation(mPanAnim);
			}
		});
        
        mBarOutAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_d_45);
        mBarOutAnim.setFillAfter(true);
        mBarOutLin = new LinearInterpolator();
        mBarOutAnim.setInterpolator(mBarOutLin);
        mBarOutAnim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				mIsRunning = false;
				mBtnPlayStart.setVisibility(View.VISIBLE);
			}
		});
        
        //初始化控件
        mViewPan = (ImageView) findViewById(R.id.img_disc);
        mViewPanBar = (ImageView) findViewById(R.id.img_bar);
        mMyGridView = (MyGridView) findViewById(R.id.gridView);
        mViewCurrentCoins = (TextView)findViewById(R.id.txt_bar_coin);
        mViewCurrentCoins.setText(mCurrentCoins + "");
        
        //
        mMyGridView.registerOnWordButtonClick(this);
        
        mViewWordsContainer = (LinearLayout) findViewById(R.id.word_select_container);
        
        //play
        mBtnPlayStart = (ImageButton) findViewById(R.id.btn_play_start);
        mBtnPlayStart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!mIsRunning) {
					handlePlayButton();
					mIsRunning = true;
				};
			} 
		});
        
        //初始化游戏数据
        initCurrentStageData();
        
        //处理删除按键事件
        handleDeleteWord();
        
        //处理提示按键事件
        handleTipWord();
    }
    
    /**
     * 开始播放音乐
     */
    private void handlePlayButton() {
    	if(mViewPanBar != null) {
    		mViewPanBar.startAnimation(mBarInAnim);
    		mBtnPlayStart.setVisibility(View.INVISIBLE);
    	}
    	
    	//播放音乐
    	MyPlayer.playSong(MainActivity.this, mCurrentSong.getFileName());
    }
    
    @Override
    public void onPause() {
    	//停止动画
    	mViewPan.clearAnimation();
    	
    	//暂停音乐
    	MyPlayer.stopSong(MainActivity.this);
    	
    	super.onPause();
    }
    
    private Song loadStageSongInfo(int index) {
    	Song song = new Song();
    	
    	String[] stage = Const.SONG_INFO[index];
    	song.setSongName(stage[Const.INDEX_SONG_NAME]);
    	song.setFileName(stage[Const.INDEX_FILE_NAME]);
    	
    	return song;
    }
    
    /**
     * 加载当前关数据
     */
    private void initCurrentStageData() {
    	//读取当前关的歌曲信息
    	mCurrentSong = loadStageSongInfo(++mCurrentIndex);
    	//初始化已选择文字框
    	mSelectWords = initSelectWord();
   	
    	LayoutParams params = new LayoutParams(120, 120);
    	
    	//清空原答案
    	mViewWordsContainer.removeAllViews();
    	//增加新答案
    	for(int i = 0; i < mSelectWords.size(); i++) {
    		mViewWordsContainer.addView(mSelectWords.get(i).mViewButton, params);
    	}
    	
    	//显示当前关索引
    	mCurrentStageView = (TextView) findViewById(R.id.txt_level);
    	if (mCurrentStageView != null) {
    		mCurrentStageView.setText((mCurrentIndex + 1) + "");
    	}
   	
    	//获得数据
    	mAllWords = initAllWord();
    	//更新数据 —— MyGridView
    	mMyGridView.updateData(mAllWords);
    	
        //一开始播放音乐
        handlePlayButton();
    }
    
    private ArrayList<WordButton> initAllWord() {
    	ArrayList<WordButton> data = new ArrayList<WordButton>();
    	
    	//获得所有待选文字
    	String[] words = generateWords();
    	
    	for(int i = 0; i < MyGridView.COUNT_WORDS; i++) {
    		WordButton button = new WordButton();
    		button.mWordString = words[i];
    		
    		data.add(button);
    	}
    	
    	return data;
    }
    
    /**
     * 初始化已选择文字框
     * 
     * @return
     */
    private ArrayList<WordButton> initSelectWord() {
    	ArrayList<WordButton> data = new ArrayList<WordButton>();
    	
    	for(int i = 0; i < mCurrentSong.getNameLength(); i++) {
    		View view = Util.getView(MainActivity.this, R.layout.self_ui_gridview_items);
    		
    		final WordButton holder = new WordButton();
    		holder.mViewButton = (Button) view.findViewById(R.id.item_btn);
    		holder.mViewButton.setTextColor(Color.WHITE);
    		holder.mViewButton.setText("");
    		holder.mIsVisiable = false;
    		holder.mViewButton.setBackgroundResource(R.drawable.game_wordblank);
    		
    		holder.mViewButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					clearAnswer(holder);
				}
			});
    		
    		data.add(holder);
    	}
    	
    	return data;
    }
    
    /**
     * 生成所有待选文字
     */
    private String[] generateWords() {
    	String[] words = new String[MyGridView.COUNT_WORDS];
    	
    	//存入歌名
    	for(int i = 0; i < mCurrentSong.getNameLength(); i++) {
    		words[i] = mCurrentSong.getNameCharacters()[i] + "";
    	}
    	
    	//存入随机文字
    	for(int i = mCurrentSong.getNameLength(); i < MyGridView.COUNT_WORDS; i++) {
    		words[i] = getRandomChar() + "";
    	}
    	
    	//打乱文字顺序
    	Random random = new Random();
    	for(int i = MyGridView.COUNT_WORDS - 1; i >= 0; i--) {
    		int index = random.nextInt(i + 1);
    		
    		String buf = words[index];
    		words[index] = words[i];
    		words[i] = buf;
    	}
    	
    	return words;
    }
    
    /**
     * 生成随机汉字
     * 
     * @return
     */
    private char getRandomChar() {
    	String str = "";
    	int highPos;
    	int lowPos;
    	
    	Random random = new Random();
    	
    	highPos = (176 + Math.abs(random.nextInt(39)));
    	lowPos = (161 + Math.abs(random.nextInt(93)));
    	
    	byte[] b = new byte[2];
    	b[0] = (Integer.valueOf(highPos)).byteValue();
    	b[1] = (Integer.valueOf(lowPos)).byteValue();
    	
    	try {
			str = new String(b, "GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return str.charAt(0);
    }
    	
	/**
	 * 检查答案
	 * 
	 * @return
	 */
	private int checkAnswer() {
		//检查长度
		for(int i = 0; i < mSelectWords.size(); i++) {
			if(0 == mSelectWords.get(i).mWordString.length()) {
				return STATUS_ANSWER_LACK;
			}
		}
		
		//答案完整，检查正确性
		StringBuffer buffer = new StringBuffer();
		for(int i = 0; i < mSelectWords.size(); i++) {
			buffer.append(mSelectWords.get(i).mWordString);
		}
		
		return (buffer.toString().equals(mCurrentSong.getSongName())) ? 
				STATUS_ANSWER_RIGHT : STATUS_ANSWER_WRONG;
	}
	
	/**
	 * 闪烁文字
	 */
	private void sparkWords() {
		TimerTask task = new TimerTask() {
			boolean mChange = false;
			int mSoarkTimes = 0;
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(++mSoarkTimes > ANSWER_SPARK_COINTS) {
							return;
						}
						
						//闪烁：交替显示红色和白色文字
						for(int i = 0; i < mSelectWords.size(); i++) {
							mSelectWords.get(i).mViewButton.setTextColor(
									mChange ? Color.RED : Color.WHITE);
						}
						
						mChange = !mChange;
					}
				});
			}
		};
		
		Timer timer = new Timer();
		timer.schedule(task, 1, 150);
	}
	
	/*
	 * 处理过关界面及事件
	 */
	private void handlePassEvent() {
		//显示过关界面
		mPassView = (LinearLayout)this.findViewById(R.id.pass_view);
		mPassView.setVisibility(View.VISIBLE);
		
		//停止未完成的动画
		mViewPan.clearAnimation();
		
		//停止未播完的音乐
		MyPlayer.stopSong(MainActivity.this);
	
		//当前关索引
		mCurrentStagePassView = (TextView) findViewById(R.id.txt_current_stage_pass);
		if (mCurrentStagePassView != null) {
			mCurrentStagePassView.setText((mCurrentIndex + 1) + "");
		}
		
		//当前关歌曲名称
		mCurrentSongNamePassView = (TextView) findViewById(R.id.txt_current_song_pass);
		if (mCurrentSongNamePassView != null) {
			mCurrentSongNamePassView.setText(mCurrentSong.getSongName());
		}
		
		//下一关按键处理
		ImageButton btnNext = (ImageButton) findViewById(R.id.btn_next_pass);
		btnNext.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (judgePassed()) {
					//进入到通关界面
					Util.startActivity(MainActivity.this, AllPassView.class);
				} else {
					//开始下一关
					mPassView.setVisibility(View.INVISIBLE);
					
					//加载关卡数据
					initCurrentStageData();
				}
			}
		});
	}
	
	/**
	 * 判断是否通关
	 * @return
	 */
	private boolean judgePassed() {
		return (mCurrentIndex == Const.SONG_INFO.length - 1); 
	}

	@Override
	public void onWordButtonClick(WordButton wordButton) {
		// TODO Auto-generated method stub
		//Toast.makeText(this, wordButton.mIndex + "", Toast.LENGTH_SHORT).show();
		setSelectWord(wordButton);
		
		//获得答案状态
		int checkResult = checkAnswer();
		
		//检查答案
		if(STATUS_ANSWER_RIGHT == checkResult) {
			//过关并获得奖励
			//Toast.makeText(this, "right", Toast.LENGTH_SHORT).show();
			handlePassEvent();
		} else if(STATUS_ANSWER_WRONG == checkResult) {
			//提示错误（闪烁）
			//Toast.makeText(this, "wrong", Toast.LENGTH_SHORT).show();
			sparkWords();
		} else if(STATUS_ANSWER_LACK == checkResult) {
			//
		}
	}
	
	private void clearAnswer(WordButton wordButton) {
		wordButton.mViewButton.setText("");
		wordButton.mWordString = "";
		wordButton.mIsVisiable = false;
		
		//设置待选框的可见性
		setButtonVisiable(mAllWords.get(wordButton.mIndex), View.VISIBLE);
	}
	
	/**
	 * 设置答案
	 * 
	 * @param wordButton
	 */
	private void setSelectWord(WordButton wordButton) {
		for(int i = 0; i < mSelectWords.size(); i++) {
			if(0 == mSelectWords.get(i).mWordString.length()) {
				//设置答案文字框的内容及可见性
				mSelectWords.get(i).mViewButton.setText(wordButton.mWordString);
				mSelectWords.get(i).mIsVisiable = true;
				mSelectWords.get(i).mWordString = wordButton.mWordString;
				//记录索引
				mSelectWords.get(i).mIndex = wordButton.mIndex;
				
				//Log
				MyLog.d(TAG, mSelectWords.get(i).mIndex + "");
				
				//设置待选框的可见性
				setButtonVisiable(wordButton, View.INVISIBLE);
				
				break;
			}
		}
	}
	
	/**
	 * 设置待选文字框是否可见
	 * 
	 * @param wordButton
	 * @param visibility
	 */
	private void setButtonVisiable(WordButton wordButton, int visibility) {
		wordButton.mViewButton.setVisibility(visibility);
		wordButton.mIsVisiable = (visibility == View.VISIBLE) ? true : false;
		
		//Log
		MyLog.d(TAG, wordButton.mIsVisiable + "");
	}
	
	/**
	 * 增加或减少指定数量的金币
	 * 
	 * @param data
	 * @return
	 */
	private boolean handleCoins(int data) {
		if(mCurrentCoins + data < 0) {
			//金币不够
			//Toast.makeText(this, "金币不够", Toast.LENGTH_SHORT).show();
			
			return false;
		} else {
			mCurrentCoins += data;
			mViewCurrentCoins.setText(mCurrentCoins + "");
			
			return true;
		}
	}
	
	/**
	 * 提示一个答案
	 */
	private void tipAnswer() {
		//减少金币数量
		if(!handleCoins(-getTipCoins())) {
			//金币不够
			//Toast.makeText(this, "金币不够", Toast.LENGTH_SHORT).show();
			
			return;
		}
		
		boolean tipWord = false;
		for(int i = 0; i < mSelectWords.size(); i++) {
			if(0 == mSelectWords.get(i).mWordString.length()) {
				//根据当前答案框条件选择对应的文字并填入
				onWordButtonClick(findIsAnswerWord(i));
				tipWord = true;
								
				break;
			}
		}
		
		//没有找到可填充答案
		if(!tipWord) {
			//闪烁文字提示用户
			sparkWords();
		}
	}
	
	/**
	 * 从配置文件中读取删除操作所要用的金币
	 * 
	 * @return
	 */
	private int getDeleteWordCoins() {
		return this.getResources().getInteger(R.integer.pay_delete_answer);
	}
	
	/**
	 * 从配置文件中读取提示操作所要用的金币
	 * 
	 * @return
	 */
	private int getTipCoins() {
		return this.getResources().getInteger(R.integer.pay_tip_answer);
	}
	
	/**
	 * 删除文字
	 */
	private void deleteOneWord() {
		//减少金币
		if(!handleCoins(-getDeleteWordCoins())) { 
			//金币不够,提示对话框
			
			return;
		}
		
		//将某一WORDBUTTON设置为不可见
		setButtonVisiable(findNotAnswerWord(), View.INVISIBLE);
	}
	
	/**
	 * 找到一个非答案的文字，且该文字可见
	 * 
	 * @return
	 */
	private WordButton findNotAnswerWord() {
		Random random = new Random();
		WordButton button = null;
		
		while(true) {
			int index = random.nextInt(MyGridView.COUNT_WORDS);
			button = mAllWords.get(index);
			
			if(button.mIsVisiable && !isAnswerWord(button)) {
				return button;
			}
		}
	}
	
	/**
	 * 找到一个答案文字
	 * @return
	 */
	private WordButton findIsAnswerWord(int index) {
		WordButton word = null;
		
		for(int i = 0; i < MyGridView.COUNT_WORDS; i++) {
			word = mAllWords.get(i);
			if(word.mWordString.equals(mCurrentSong.getNameCharacters()[index] + "")) {
				return word;
			}
		}
		
		return null;
	}
	
	/**
	 * 判断某文字是否为答案
	 * 
	 * @param button
	 * @return
	 */
	private boolean isAnswerWord(WordButton word) {
		boolean result = false;
		
		for(int i = 0; i < mCurrentSong.getNameLength(); i++) {
			if(word.mWordString.equals("" + mCurrentSong.getNameCharacters()[i])) {
				result = true;
				break;
			}
		}
		
		return result;
	}
	
	/**
	 * 处理删除待选文字事件
	 */
	private void handleDeleteWord() {
		ImageButton button = (ImageButton) findViewById(R.id.img_delete);
		button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				deleteOneWord();
			}
		});
	}

	/**
	 * 处理提示事件
	 */
	private void handleTipWord() {
		ImageButton button = (ImageButton) findViewById(R.id.img_tip);
		button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tipAnswer();
			}
		});
	}
}
