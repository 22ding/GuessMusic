package com.zhang.guessmusic.ui;

import com.zhang.guessmusic.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 通关界面
 * 
 * @author ZhangJY
 *
 */
public class AllPassView extends Activity{
	@Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.all_pass_view);
        
        //隐藏右上角金币
        FrameLayout view = (FrameLayout) findViewById(R.id.layout_bar_coin);
        view.setVisibility(View.INVISIBLE);
	}
}
