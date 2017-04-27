package com.example.musicplayer;

import java.util.ArrayList;

import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MusicAdapter extends BaseAdapter {

	ArrayList<MyFile> MyFile_list = new ArrayList<MyFile>();
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return MyFile_list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return MyFile_list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}
	public void setData(ArrayList<MyFile> MyFile_list){
		this.MyFile_list = MyFile_list;
		notifyDataSetChanged();
	}
	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		ViewHolder holder= null;
		if(arg1 == null){
			holder = new ViewHolder();
			arg1 = LayoutInflater.from(arg2.getContext()).inflate(R.layout.music_item, null);
			holder.tv = (TextView) arg1.findViewById(R.id.tv);
			holder.iv = (ImageView) arg1.findViewById(R.id.iv);
			arg1.setTag(holder);
		}else{
			holder = (ViewHolder) arg1.getTag();
			
		}
		MyFile myFile = MyFile_list.get(arg0);
		if(myFile.isthisSong||myFile.ischecked){
			holder.iv.setVisibility(View.VISIBLE);
			AnimationDrawable ad = (AnimationDrawable) holder.iv.getBackground();
			if(!myFile.isPusing){
				ad.start();
			}else{
				ad.stop();
			}
		}else{
			holder.iv.setVisibility(View.GONE);
		}
		holder.tv.setText(myFile.file.getName());
		return arg1;
	}
	
	class ViewHolder{
		TextView tv;
		ImageView iv;
	}

}
