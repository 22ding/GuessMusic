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
	
	//��˸����
	public static final int ANSWER_SPARK_COINTS = 5;
	
	// ��Ƭ��ض���
	private Animation mPanAnim;
	private LinearInterpolator mPanLin;
	
	// ������ض���
	private Animation mBarInAnim;
	private LinearInterpolator mBarInLin;
	private Animation mBarOutAnim;
	private LinearInterpolator mBarOutLin;
	
	private ImageView mViewPan;
	private ImageView mViewPanBar;
	
	//play�����¼�
    private ImageButton mBtnPlayStart;
    
    //flag
    private boolean mIsRunning = false;
    
    //���ֿ�����
    private ArrayList<WordButton> mAllWords;
    private ArrayList<WordButton> mSelectWords;
    private MyGridView mMyGridView;
    
    //��ѡ�����ֿ�UI����
    private LinearLayout mViewWordsContainer;
    
    //��ǰ�ĸ���
    private Song mCurrentSong;
    
    //��ǰ�ص�����
    private int mCurrentIndex = -1;
    
    //���ؽ���
    private View mPassView;
    
    //��ǰ�������
    private int mCurrentCoins = Const.TOTAL_COINS;
    
    //���VIEW
    private TextView mViewCurrentCoins;
    
    //��ǰ������
    private TextView mCurrentStagePassView;
    private TextView mCurrentStageView;
    
    //��ǰ�ظ�������
    private TextView mCurrentSongNamePassView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //��ʼ������
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
        
        //��ʼ���ؼ�
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
        
        //��ʼ����Ϸ����
        initCurrentStageData();
        
        //����ɾ�������¼�
        handleDeleteWord();
        
        //������ʾ�����¼�
        handleTipWord();
    }
    
    /**
     * ��ʼ��������
     */
    private void handlePlayButton() {
    	if(mViewPanBar != null) {
    		mViewPanBar.startAnimation(mBarInAnim);
    		mBtnPlayStart.setVisibility(View.INVISIBLE);
    	}
    	
    	//��������
    	MyPlayer.playSong(MainActivity.this, mCurrentSong.getFileName());
    }
    
    @Override
    public void onPause() {
    	//ֹͣ����
    	mViewPan.clearAnimation();
    	
    	//��ͣ����
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
     * ���ص�ǰ������
     */
    private void initCurrentStageData() {
    	//��ȡ��ǰ�صĸ�����Ϣ
    	mCurrentSong = loadStageSongInfo(++mCurrentIndex);
    	//��ʼ����ѡ�����ֿ�
    	mSelectWords = initSelectWord();
   	
    	LayoutParams params = new LayoutParams(120, 120);
    	
    	//���ԭ��
    	mViewWordsContainer.removeAllViews();
    	//�����´�
    	for(int i = 0; i < mSelectWords.size(); i++) {
    		mViewWordsContainer.addView(mSelectWords.get(i).mViewButton, params);
    	}
    	
    	//��ʾ��ǰ������
    	mCurrentStageView = (TextView) findViewById(R.id.txt_level);
    	if (mCurrentStageView != null) {
    		mCurrentStageView.setText((mCurrentIndex + 1) + "");
    	}
   	
    	//�������
    	mAllWords = initAllWord();
    	//�������� ���� MyGridView
    	mMyGridView.updateData(mAllWords);
    	
        //һ��ʼ��������
        handlePlayButton();
    }
    
    private ArrayList<WordButton> initAllWord() {
    	ArrayList<WordButton> data = new ArrayList<WordButton>();
    	
    	//������д�ѡ����
    	String[] words = generateWords();
    	
    	for(int i = 0; i < MyGridView.COUNT_WORDS; i++) {
    		WordButton button = new WordButton();
    		button.mWordString = words[i];
    		
    		data.add(button);
    	}
    	
    	return data;
    }
    
    /**
     * ��ʼ����ѡ�����ֿ�
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
     * �������д�ѡ����
     */
    private String[] generateWords() {
    	String[] words = new String[MyGridView.COUNT_WORDS];
    	
    	//�������
    	for(int i = 0; i < mCurrentSong.getNameLength(); i++) {
    		words[i] = mCurrentSong.getNameCharacters()[i] + "";
    	}
    	
    	//�����������
    	for(int i = mCurrentSong.getNameLength(); i < MyGridView.COUNT_WORDS; i++) {
    		words[i] = getRandomChar() + "";
    	}
    	
    	//��������˳��
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
     * �����������
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
	 * ����
	 * 
	 * @return
	 */
	private int checkAnswer() {
		//��鳤��
		for(int i = 0; i < mSelectWords.size(); i++) {
			if(0 == mSelectWords.get(i).mWordString.length()) {
				return STATUS_ANSWER_LACK;
			}
		}
		
		//�������������ȷ��
		StringBuffer buffer = new StringBuffer();
		for(int i = 0; i < mSelectWords.size(); i++) {
			buffer.append(mSelectWords.get(i).mWordString);
		}
		
		return (buffer.toString().equals(mCurrentSong.getSongName())) ? 
				STATUS_ANSWER_RIGHT : STATUS_ANSWER_WRONG;
	}
	
	/**
	 * ��˸����
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
						
						//��˸��������ʾ��ɫ�Ͱ�ɫ����
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
	 * ������ؽ��漰�¼�
	 */
	private void handlePassEvent() {
		//��ʾ���ؽ���
		mPassView = (LinearLayout)this.findViewById(R.id.pass_view);
		mPassView.setVisibility(View.VISIBLE);
		
		//ֹͣδ��ɵĶ���
		mViewPan.clearAnimation();
		
		//ֹͣδ���������
		MyPlayer.stopSong(MainActivity.this);
	
		//��ǰ������
		mCurrentStagePassView = (TextView) findViewById(R.id.txt_current_stage_pass);
		if (mCurrentStagePassView != null) {
			mCurrentStagePassView.setText((mCurrentIndex + 1) + "");
		}
		
		//��ǰ�ظ�������
		mCurrentSongNamePassView = (TextView) findViewById(R.id.txt_current_song_pass);
		if (mCurrentSongNamePassView != null) {
			mCurrentSongNamePassView.setText(mCurrentSong.getSongName());
		}
		
		//��һ�ذ�������
		ImageButton btnNext = (ImageButton) findViewById(R.id.btn_next_pass);
		btnNext.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (judgePassed()) {
					//���뵽ͨ�ؽ���
					Util.startActivity(MainActivity.this, AllPassView.class);
				} else {
					//��ʼ��һ��
					mPassView.setVisibility(View.INVISIBLE);
					
					//���عؿ�����
					initCurrentStageData();
				}
			}
		});
	}
	
	/**
	 * �ж��Ƿ�ͨ��
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
		
		//��ô�״̬
		int checkResult = checkAnswer();
		
		//����
		if(STATUS_ANSWER_RIGHT == checkResult) {
			//���ز���ý���
			//Toast.makeText(this, "right", Toast.LENGTH_SHORT).show();
			handlePassEvent();
		} else if(STATUS_ANSWER_WRONG == checkResult) {
			//��ʾ������˸��
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
		
		//���ô�ѡ��Ŀɼ���
		setButtonVisiable(mAllWords.get(wordButton.mIndex), View.VISIBLE);
	}
	
	/**
	 * ���ô�
	 * 
	 * @param wordButton
	 */
	private void setSelectWord(WordButton wordButton) {
		for(int i = 0; i < mSelectWords.size(); i++) {
			if(0 == mSelectWords.get(i).mWordString.length()) {
				//���ô����ֿ�����ݼ��ɼ���
				mSelectWords.get(i).mViewButton.setText(wordButton.mWordString);
				mSelectWords.get(i).mIsVisiable = true;
				mSelectWords.get(i).mWordString = wordButton.mWordString;
				//��¼����
				mSelectWords.get(i).mIndex = wordButton.mIndex;
				
				//Log
				MyLog.d(TAG, mSelectWords.get(i).mIndex + "");
				
				//���ô�ѡ��Ŀɼ���
				setButtonVisiable(wordButton, View.INVISIBLE);
				
				break;
			}
		}
	}
	
	/**
	 * ���ô�ѡ���ֿ��Ƿ�ɼ�
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
	 * ���ӻ����ָ�������Ľ��
	 * 
	 * @param data
	 * @return
	 */
	private boolean handleCoins(int data) {
		if(mCurrentCoins + data < 0) {
			//��Ҳ���
			//Toast.makeText(this, "��Ҳ���", Toast.LENGTH_SHORT).show();
			
			return false;
		} else {
			mCurrentCoins += data;
			mViewCurrentCoins.setText(mCurrentCoins + "");
			
			return true;
		}
	}
	
	/**
	 * ��ʾһ����
	 */
	private void tipAnswer() {
		//���ٽ������
		if(!handleCoins(-getTipCoins())) {
			//��Ҳ���
			//Toast.makeText(this, "��Ҳ���", Toast.LENGTH_SHORT).show();
			
			return;
		}
		
		boolean tipWord = false;
		for(int i = 0; i < mSelectWords.size(); i++) {
			if(0 == mSelectWords.get(i).mWordString.length()) {
				//���ݵ�ǰ�𰸿�����ѡ���Ӧ�����ֲ�����
				onWordButtonClick(findIsAnswerWord(i));
				tipWord = true;
								
				break;
			}
		}
		
		//û���ҵ�������
		if(!tipWord) {
			//��˸������ʾ�û�
			sparkWords();
		}
	}
	
	/**
	 * �������ļ��ж�ȡɾ��������Ҫ�õĽ��
	 * 
	 * @return
	 */
	private int getDeleteWordCoins() {
		return this.getResources().getInteger(R.integer.pay_delete_answer);
	}
	
	/**
	 * �������ļ��ж�ȡ��ʾ������Ҫ�õĽ��
	 * 
	 * @return
	 */
	private int getTipCoins() {
		return this.getResources().getInteger(R.integer.pay_tip_answer);
	}
	
	/**
	 * ɾ������
	 */
	private void deleteOneWord() {
		//���ٽ��
		if(!handleCoins(-getDeleteWordCoins())) { 
			//��Ҳ���,��ʾ�Ի���
			
			return;
		}
		
		//��ĳһWORDBUTTON����Ϊ���ɼ�
		setButtonVisiable(findNotAnswerWord(), View.INVISIBLE);
	}
	
	/**
	 * �ҵ�һ���Ǵ𰸵����֣��Ҹ����ֿɼ�
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
	 * �ҵ�һ��������
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
	 * �ж�ĳ�����Ƿ�Ϊ��
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
	 * ����ɾ����ѡ�����¼�
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
	 * ������ʾ�¼�
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
