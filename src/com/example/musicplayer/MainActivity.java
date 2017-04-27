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

	private CheckBox playOrpause; //���Ż���ͣ��ť
	private MediaPlayer player = new MediaPlayer(); 
	private ImageView zhuandong;	//ת����ͼƬ
	private int[] bgs = { R.drawable.main_bg01, R.drawable.main_bg02,
			R.drawable.main_bg03, R.drawable.main_bg05, R.drawable.main_bg06,
			R.drawable.mybg }; //������Դ
	int SongTime = 0;	//����ʱ��
	int thisSong = 0;	//�����е�ǰ���ŵĸ������±�
	private ListView lv_music;	//�����б�
	private MusicAdapter adapter;	//����������
	String save_music = "";		//������·��ƴ�ӳ��ַ������ڱ��浽�ļ���
	private Animation animation;	//������֡������ʼ��
	ArrayList<MyFile> mf_list = new ArrayList<MyFile>();	//��Ÿ����ļ���
	private SeekBar sb;	//����������
	private File files;		//filesĿ¼�µ��ļ�
	private int currentPosition;	//��ǰ���Ž���
	private TextView tv_start;	//�÷�:���ʾ��ǰ���Ž���
	private TextView tv_end;	//�÷�:���ʾ������ʱ��
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
		//�������
		findViewById(R.id.rl_bg).setBackgroundResource(bgs[new Random().nextInt(6)]);
		initviews();
		initData();
		//���ֲ�����ɼ���
		player.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer arg0) {
				thisSong++;//�����б��е���һ��
				//������б��е����һ�ײ�����ɺ��򲥷ŵ�һ��
				thisSong = thisSong > mf_list.size() - 1 ? 0 : thisSong;
				MyFile myfile = (MyFile) adapter.getItem(thisSong);
				playmusic(myfile);
			}
		});
	}

	private void initData() {
		initmusic();
		getMusic();
		files = new File(getFilesDir(), "musiclist.txt");// ��ȡfilesĿ¼�µ��ļ�
		if (!files.exists()) {
			try {
				files.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// �������ҵ�filesĿ¼�µ��ļ���������ͨ��IO����ȡ���ݲ���
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
	 * ������·�������б�
	 */
	private void getMusic() {
		if (mf_list.size()>0) {
			return;
		}
		File dir = getFilesDir();
		File file = new File(dir, "�ζ�Ұ-��С��.mp3");
		MyFile myFile = new MyFile(false, file);
		mf_list.add(0,myFile);
	}

	/**
	 * ��ʼ���������ֵ�date/date/����Ŀ¼��
	 */
	private void initmusic() {
		File dir = getFilesDir();
		File file = new File(dir, "�ζ�Ұ-��С��.mp3");
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
		animation.setInterpolator(new LinearInterpolator()); // ���ò�����
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
				//����˲��Ű�ť
				if (isChecked) {
					sb.setProgress(currentPosition);//�������ͣ����,�����֮ǰ���Ȳ���
					player.start();
					mf_list.get(thisSong).isPusing = false;
//					if (animation != null) {  
					    zhuandong.startAnimation(animation);  
//					}
				} else {
					mf_list.get(thisSong).isPusing = true;
					currentPosition = player.getCurrentPosition();//��ͣʱ��ȡ��ǰ���Ž���
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
				//�û��϶�������
				if (fromUser) {
					player.seekTo(progress);// ���ݸ����ٷֱ����ý�����
				}
			}
		});
	}

	/**
	 * ��������б���Ŀʱ��������
	 * @param myFile ��װ�˸�����Ϣ�Ķ���
	 */
	protected void playmusic(MyFile myFile) {
		//�����и�ȥ��Ϊ��ʼ��״̬
		for (MyFile mf : mf_list) {
			mf.isthisSong = false;
		}
		myFile.isthisSong = true;
		zhuandong.clearAnimation();
		player.stop();	//ֹͣ
		player.reset();	//����
		try {
			h_updata.removeMessages(100);
			player.setDataSource(myFile.file.getAbsolutePath());
			player.prepare();	//׼������
			SongTime = player.getDuration();
			tv_end.setText(timeParse(SongTime));	//���õ�ǰ������ʱ��,�� ��:�� ��ʾ
			sb.setMax(SongTime);// �������ĳ���ʱ�����ø�������
			player.start();		//��ʼ����
			if (animation != null) {  
			    zhuandong.startAnimation(animation);  
			}
			adapter.notifyDataSetChanged();	//ˢ�½�������
			h_updata.sendEmptyMessage(100);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * �����ֵĺ���ֵת�ɷ�:��
	 * @param duration ���ֵĺ���ֵ
	 * @return ��:��
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
	 * ��һ��
	 * @param v
	 */
	public void previous(View v) {
		thisSong--;
		//����Ǽ����еĵ�һ��,�򲥷ż����е����һ��,ͬʱ���⼯��Խ��
		thisSong = thisSong < 0 ? mf_list.size() - 1 : thisSong;
		MyFile myfile = mf_list.get(thisSong);
		playmusic(myfile);
		playOrpause.setChecked(true);
		adapter.notifyDataSetChanged();

	}

	/**
	 * ��һ��
	 * @param v
	 */
	public void next(View v) {
		thisSong++;
		//����Ǽ����е����һ��,�򲥷ż����еĵ�һ��,ͬʱ���⼯��Խ��
		thisSong = thisSong > mf_list.size() - 1 ? 0 : thisSong;
		MyFile myfile = mf_list.get(thisSong);
		playmusic(myfile);
		playOrpause.setChecked(true);
		adapter.notifyDataSetChanged();
	}

	/**
	 * �ļ���(SD��Ŀ¼)
	 * @param v
	 */
	public void localfile(View v) {
		Intent i = new Intent(this, FileActivity.class);
		startActivityForResult(i, 1000);
	}

	/**
	 * �������еĸ���·����ӵ�����(�����б�)��
	 * @param strings ����·���ļ���
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
			// �и�
			String[] strings = string.split("@");
			//�������еĸ���·����ӵ�����(�����б�)��
			getMusicPathList(strings);
			adapter.setData(mf_list);	//�Ѽ��ϴ���������
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	//�˳�����֮ǰ������·�����浽�ļ���
	@Override
	protected void onDestroy() {
		mf_list.remove(0);
		for (MyFile mf : mf_list) {// �������ļ���·������һ����@�����ӵ��ַ���
			save_music = save_music + mf.file.getAbsolutePath() + "@";
		}
		// ��ʼ������
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
