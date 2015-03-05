package com.zhang.guessmusic.myui;

import java.util.ArrayList;

import com.zhang.guessmusic.R;
import com.zhang.guessmusic.model.IWordButtonClickListener;
import com.zhang.guessmusic.model.WordButton;
import com.zhang.guessmusic.util.Util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

public class MyGridView extends GridView{
	public final static int COUNT_WORDS = 24;
	
	private ArrayList<WordButton> mArrayList = new ArrayList<WordButton>();
	private MyGridAdapter mAdapter;
	
	private Context mContext;
	
	private Animation mScaleAnim;
	
	private IWordButtonClickListener mWordButtonListener;

	public MyGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
		
		mAdapter = new MyGridAdapter();
		this.setAdapter(mAdapter);
	}
	
	public void updateData(ArrayList<WordButton> list) {
		mArrayList = list;
		
		//������������Դ
		setAdapter(mAdapter);
	}

	class MyGridAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mArrayList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mArrayList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup p) {
			// TODO Auto-generated method stub
			final WordButton holder;
			
			if(v == null) {
				v = Util.getView(mContext, R.layout.self_ui_gridview_items);
				
				holder = mArrayList.get(position);
				
				//���ض���
				mScaleAnim = AnimationUtils.loadAnimation(mContext, R.anim.scale);				
				//���ö������ӳ�ʱ��
				mScaleAnim.setStartOffset(position * 100);
				
				holder.mIndex = position;
				
				if(null == holder.mViewButton) {
					holder.mViewButton = (Button) v.findViewById(R.id.item_btn);
					holder.mViewButton.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							mWordButtonListener.onWordButtonClick(holder);
						}
					});
				}				
				
				v.setTag(holder);
			} else {
				holder = (WordButton) v.getTag();
			}
			
			holder.mViewButton.setText(holder.mWordString);
			
			//���Ŷ���
			v.startAnimation(mScaleAnim);
		
			return v;
		}		
	}
	
	/*
	 *  ע������ӿ�
	 */
	public void registerOnWordButtonClick(IWordButtonClickListener listener) {
		mWordButtonListener = listener;
	}
}
