package com.example.musicplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class FileActivity extends Activity {

	private ListView lv_file;
	private ArrayList<MyFile> dir_list = new ArrayList<MyFile>();//存储各级目录的路径
	private ArrayList<MyFile> mp3Filelist = new ArrayList<MyFile>();//存储搜索出来的MP3文件的路径
	private FileAdapter adapter;
	String paths = "";
	private File sdFile;
	private View loading;
	private TextView tvSong;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			File file = (File) msg.obj;
			tvSong.setText("歌曲位置:"+file.getAbsolutePath().toString());
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_layout);
		initView();
		initData();
		
	}

	private void initView() {
		lv_file = (ListView) findViewById(R.id.lv_fileActivity);
		loading = findViewById(R.id.loading);
		tvSong = (TextView) findViewById(R.id.song);
	}
	
	private void initData() {
		sdFile = Environment.getExternalStorageDirectory();
		dir_list.add(new MyFile(false, sdFile));	//存储sd卡根路径
		
		adapter = new FileAdapter();
		adapter.setData(getMyFilelist(sdFile)); // 将sd根目录的文件夹和文件数据传给适配器
		lv_file.setAdapter(adapter);
		
		lv_file.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				MyFile file = adapter.getItem(position);
				if (file.file.isDirectory()) {
					dir_list.add(file);	//第二次点击存储二级目录,第三次点击存储三级目录...依次存储
					adapter.setData(getMyFilelist(file.file));
				} else {
					if (file.file.getName().endsWith(".mp3")) {
						file.ischecked = !file.ischecked;
						adapter.notifyDataSetChanged();
					}
				}
			}
		});

	}

	/**
	 * 该方法获取一个目录里的目录和文件
	 * 
	 * @param 路径
	 * @return 返回一个目录里的目录和文件的集合
	 */
	public ArrayList<MyFile> getMyFilelist(File file) {
		ArrayList<MyFile> myFile_lists = new ArrayList<MyFile>();
		File[] files = file.listFiles();
		for (File f : files) {
			myFile_lists.add(new MyFile(false, f));
		}
		return myFile_lists;
	}

	/**
	 * 确定按钮
	 * 
	 * @param v
	 */
	public void sure(View v) {
		Intent data = new Intent();
		for (MyFile mf : adapter.MyFile_list) {
			if (mf.ischecked) {
				if (mf.file.getAbsolutePath().toString().equals("")) {
					break;
				}
				paths = paths + mf.file.getAbsolutePath() + "@";
			}
		}
		data.putExtra("paths", paths);
		setResult(2000, data);
		finish();
	}

	/**
	 * 返回上一级
	 * 
	 * @param v
	 */
	public void backToPreLevel(View v) {
		if (dir_list.size() == 1) {
			return;
		}
		ArrayList<MyFile> myFilelist = getMyFilelist(dir_list.get(dir_list.size() - 2).file);
		adapter.setData(myFilelist);
		adapter.notifyDataSetChanged();
		dir_list.remove(dir_list.size() - 1);
	}

	/**
	 * 搜索按钮
	 * 
	 * @param v
	 */
	public void search(View v) {
		loading.setVisibility(View.VISIBLE);
		mp3Filelist.clear();
		dir_list.add(new MyFile(false, sdFile));	//占一个位置
		new Thread(){
			public void run() {
//				extracted(new File(sdFile+"/MIUI"));
				extracted(sdFile);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						loading.setVisibility(View.GONE);
						adapter.setData(mp3Filelist);
					}
				});
			};
		}.start();
		
	}

	/**
	 * 搜索文件,递归调用
	 * @param file
	 */
	public void extracted(File file) {
		File[] files = file.listFiles();
		if (files == null) {
			return;
		}
		for (File f : files) {
			if (f.isFile() && f.getName().endsWith(".mp3")) {
				SystemClock.sleep(10+new Random().nextInt(10));
				Message msg = Message.obtain();
				msg.obj = f;
				mHandler.sendMessage(msg);
				mp3Filelist.add(new MyFile(false, f));
			} else if (f.isDirectory()) {
				extracted(f);
			}
		}
	}

	/**
	 * 全选按钮
	 * 
	 * @param v
	 */
	public void chooseAll(View v) {
		int index = adapter.getCount();
		for (int i = 0; i < index; i++) {
			MyFile file = (MyFile) adapter.getItem(i);
			if (file.file.isDirectory()) {
				continue;
			} else if (file.file.getAbsolutePath().toString().endsWith("mp3")) {
				file.ischecked = true;
				adapter.notifyDataSetChanged();
			}
		}

	}

	/**
	 * 取消全选
	 * 
	 * @param v
	 */
	public void cancelAll(View v) {
		int index = adapter.getCount();
		for (int i = 0; i < index; i++) {
			MyFile file = (MyFile) adapter.getItem(i);
			if (file.file.isDirectory()) {
				continue;
			} else if (file.file.getAbsolutePath().toString().endsWith("mp3")) {

				file.ischecked = false;
				adapter.notifyDataSetChanged();
			}
		}
	}

	/**
	 * 反选按钮
	 * 
	 * @param v
	 */
	public void fanxuan(View v) {
		int index = adapter.getCount();
		for (int i = 0; i < index; i++) {
			MyFile file = (MyFile) adapter.getItem(i);
			if (file.file.isDirectory()) {
				continue;
			} else if (file.file.getAbsolutePath().toString().endsWith("mp3")) {
				if (file.ischecked) {
					file.ischecked = false;
				} else {
					file.ischecked = true;
				}
				adapter.notifyDataSetChanged();
			}

		}
	}

}
