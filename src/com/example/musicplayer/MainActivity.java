package com.example.musicplayer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

@SuppressLint("HandlerLeak") public class MainActivity extends Activity {

	private CheckBox playOrpause; //播放或暂停按钮
	private MediaPlayer player = new MediaPlayer(); 
	private ImageView zhuandong;	//转动的图片
	private int[] bgs = { R.drawable.main_bg01, R.drawable.main_bg02,
			R.drawable.main_bg03, R.drawable.main_bg05, R.drawable.main_bg06,
			R.drawable.mybg }; //背景资源
	int SongTime = 0;	//歌曲时长
	int thisSong = 0;	//集合中当前播放的歌曲的下标
	private ListView lv_music;	//播放列表
	private MusicAdapter adapter;	//歌曲适配器
	String save_music = "";		//将歌曲路劲拼接成字符串用于保存到文件中
	private Animation animation;	//跳动的帧动画初始化
	ArrayList<MyFile> mf_list = new ArrayList<MyFile>();	//存放歌曲的集合
	private SeekBar sb;	//歌曲进度条
	private File files;		//files目录下的文件
	private int currentPosition;	//当前播放进度
	private TextView tv_start;	//用分:秒表示当前播放进度
	private TextView tv_end;	//用分:秒表示歌曲总时长
	Handler h_updata = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			sb.setProgress(player.getCurrentPosition());
			tv_start.setText(timeParse(player.getCurrentPosition()));
			h_updata.sendEmptyMessageDelayed(100, 200);
			super.handleMessage(msg);
		}
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//随机背景
		findViewById(R.id.rl_bg).setBackgroundResource(bgs[new Random().nextInt(6)]);
		initviews();
		initData();
		//音乐播放完成监听
		player.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer arg0) {
				thisSong++;//播放列表中的下一首
				//如果是列表中的最后一首播放完成后则播放第一首
				thisSong = thisSong > mf_list.size() - 1 ? 0 : thisSong;
				MyFile myfile = (MyFile) adapter.getItem(thisSong);
				playmusic(myfile);
			}
		});
	}

	private void initData() {
		initmusic();
		getMusic();
		files = new File(getFilesDir(), "musiclist.txt");// 获取files目录下的文件
		if (!files.exists()) {
			try {
				files.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 以上是找到files目录下的文件，以下是通过IO流读取数据操作
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int len = 0;
			byte[] buf = new byte[1024];
			FileInputStream fis = new FileInputStream(files);
			while ((len = fis.read(buf)) != -1) {
				baos.write(buf, 0, len);
			}
			fis.close();
			String s = new String(baos.toByteArray());
			String[] strings = s.split("@");
			getMusicPathList(strings);
			adapter.setData(mf_list);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
	/**
	 * 将音乐路径加入列表
	 */
	private void getMusic() {
		if (mf_list.size()>0) {
			return;
		}
		File dir = getFilesDir();
		File file = new File(dir, "宋冬野-董小姐.mp3");
		MyFile myFile = new MyFile(false, file);
		mf_list.add(0,myFile);
	}

	/**
	 * 初始化加载音乐到date/date/工程目录下
	 */
	private void initmusic() {
		File dir = getFilesDir();
		File file = new File(dir, "宋冬野-董小姐.mp3");
		if (file.exists()) {
			return;
		}
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			is = getAssets().open("dxj.mp3");
			fos = new FileOutputStream(file);
			byte[] b = new byte[1024];
			int len = 0;
			while ((len=is.read(b))>0) {
				fos.write(b, 0, len);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (is!=null&&fos!=null) {
				try {
					is.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void initviews() {
		zhuandong = (ImageView) findViewById(R.id.zhuandong);
		animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.tip);
		animation.setInterpolator(new LinearInterpolator()); // 设置插入器
		sb = (SeekBar) findViewById(R.id.sb);
		tv_start = (TextView) findViewById(R.id.tv_start);
		tv_end = (TextView) findViewById(R.id.tv_end);
		lv_music = (ListView) findViewById(R.id.lv_music);
		playOrpause = (CheckBox) findViewById(R.id.playOrpause);
		adapter = new MusicAdapter();
		lv_music.setAdapter(adapter);
		
		playOrpause.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				//点击了播放按钮
				if (isChecked) {
					sb.setProgress(currentPosition);//如果是暂停后点击,则继续之前进度播放
					player.start();
					mf_list.get(thisSong).isPusing = false;
//					if (animation != null) {  
					    zhuandong.startAnimation(animation);  
//					}
				} else {
					mf_list.get(thisSong).isPusing = true;
					currentPosition = player.getCurrentPosition();//暂停时获取当前播放进度
					player.pause();
					zhuandong.clearAnimation();
				}
				adapter.notifyDataSetChanged();
			}
		});
		
		lv_music.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {
				thisSong = position;
				playOrpause.setChecked(true);
				MyFile myFile = mf_list.get(position);
				playmusic(myFile);
			}
		});
		
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				//用户拖动进度条
				if (fromUser) {
					player.seekTo(progress);// 根据歌曲百分比设置进度条
				}
			}
		});
	}

	/**
	 * 点击播放列表条目时播放音乐
	 * @param myFile 封装了歌曲信息的对象
	 */
	protected void playmusic(MyFile myFile) {
		//将所有歌去置为初始化状态
		for (MyFile mf : mf_list) {
			mf.isthisSong = false;
		}
		myFile.isthisSong = true;
		zhuandong.clearAnimation();
		player.stop();	//停止
		player.reset();	//重置
		try {
			h_updata.removeMessages(100);
			player.setDataSource(myFile.file.getAbsolutePath());
			player.prepare();	//准备播放
			SongTime = player.getDuration();
			tv_end.setText(timeParse(SongTime));	//设置当前歌曲总时长,用 分:秒 表示
			sb.setMax(SongTime);// 将歌曲的持续时长设置个进度条
			player.start();		//开始播放
			if (animation != null) {  
			    zhuandong.startAnimation(animation);  
			}
			adapter.notifyDataSetChanged();	//刷新界面数据
			h_updata.sendEmptyMessage(100);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 将音乐的毫秒值转成分:秒
	 * @param duration 音乐的毫秒值
	 * @return 分:秒
	 */
	public String timeParse(long duration) {  
	    String time = "" ;  
	          
	    long minute = duration / 60000 ;  
	    long seconds = duration % 60000 ;  
	      
	    long second = Math.round((float)seconds/1000) ;  
	          
	    if( minute < 10 ){  
	        time += "0" ;  
	    }  
	    time += minute+":" ;   
	          
	    if( second < 10 ){  
	        time += "0" ;  
	    }  
	    time += second ;  
	          
	    return time ;  
	}  
	
	/**
	 * 上一首
	 * @param v
	 */
	public void previous(View v) {
		thisSong--;
		//如果是集合中的第一首,则播放集合中的最后一首,同时避免集合越界
		thisSong = thisSong < 0 ? mf_list.size() - 1 : thisSong;
		MyFile myfile = mf_list.get(thisSong);
		playmusic(myfile);
		playOrpause.setChecked(true);
		adapter.notifyDataSetChanged();

	}

	/**
	 * 下一首
	 * @param v
	 */
	public void next(View v) {
		thisSong++;
		//如果是集合中的最后一首,则播放集合中的第一首,同时避免集合越界
		thisSong = thisSong > mf_list.size() - 1 ? 0 : thisSong;
		MyFile myfile = mf_list.get(thisSong);
		playmusic(myfile);
		playOrpause.setChecked(true);
		adapter.notifyDataSetChanged();
	}

	/**
	 * 文件夹(SD卡目录)
	 * @param v
	 */
	public void localfile(View v) {
		Intent i = new Intent(this, FileActivity.class);
		startActivityForResult(i, 1000);
	}

	/**
	 * 将数组中的歌曲路径添加到集合(播放列表)中
	 * @param strings 歌曲路径的集合
	 */
	public void getMusicPathList(String[] strings) {
		for (String s : strings) {
			if (s.equals("")) {
				break;
			}
			File file = new File(s);
			mf_list.add(new MyFile(false, file));
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1000 && resultCode == 2000) {
			String string = data.getStringExtra("paths");
			// 切割
			String[] strings = string.split("@");
			//将数组中的歌曲路径添加到集合(播放列表)中
			getMusicPathList(strings);
			adapter.setData(mf_list);	//把集合传给适配器
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	//退出程序之前将音乐路径保存到文件中
	@Override
	protected void onDestroy() {
		mf_list.remove(0);
		for (MyFile mf : mf_list) {// 将音乐文件的路径连成一条“@”连接的字符串
			save_music = save_music + mf.file.getAbsolutePath() + "@";
		}
		// 开始存数据
		try {
			FileOutputStream fos = new FileOutputStream(files);
			fos.write(save_music.getBytes());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}
}
