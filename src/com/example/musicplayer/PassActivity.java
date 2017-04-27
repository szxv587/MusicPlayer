package com.example.musicplayer;

import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

public class PassActivity extends Activity {
	private int[] bgs = {R.drawable.main_bg01,
			R.drawable.main_bg02,R.drawable.main_bg03,
			R.drawable.main_bg04,R.drawable.main_bg05,
			R.drawable.main_bg06,R.drawable.mybg};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pass);
		findViewById(R.id.ll_bg).setBackgroundResource(bgs[new Random().nextInt(7)]);
		gotoMain();
	}
	
	/**
	 * 睡眠2s后跳转主页面
	 */
	private void gotoMain() {
		new Thread(){
			public void run() {
				SystemClock.sleep(2000);
				startActivity(new Intent(PassActivity.this,MainActivity.class));
				finish();
			};
		}.start();
	}
}
