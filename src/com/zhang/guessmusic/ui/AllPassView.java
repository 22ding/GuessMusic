package com.zhang.guessmusic.ui;

import com.zhang.guessmusic.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

/**
 * ͨ�ؽ���
 * 
 * @author ZhangJY
 *
 */
public class AllPassView extends Activity{
	@Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.all_pass_view);
        
        //�������Ͻǽ��
        FrameLayout view = (FrameLayout) findViewById(R.id.layout_bar_coin);
        view.setVisibility(View.INVISIBLE);
	}
}
