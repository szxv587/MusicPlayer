package com.example.musicplayer;

import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileAdapter extends BaseAdapter {

	ArrayList<MyFile> MyFile_list = new ArrayList<MyFile>();
	@Override
	public int getCount() {
		return MyFile_list.size();
	}

	@Override
	public MyFile getItem(int position) {
		return MyFile_list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	public void setData(ArrayList<MyFile> MyFile_list){
		this.MyFile_list = MyFile_list;
		notifyDataSetChanged();
	}
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder= null;
		if(view == null){
			holder = new ViewHolder();
			view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item, null);
			holder.tv = (TextView) view.findViewById(R.id.tv);
			holder.iv = (ImageView) view.findViewById(R.id.iv);
			view.setTag(holder);
		}else{
			holder = (ViewHolder) view.getTag();
			
		}
		MyFile myFile = MyFile_list.get(position);
		if(myFile.ischecked){
			holder.iv.setVisibility(View.VISIBLE);
		}else{
			holder.iv.setVisibility(View.INVISIBLE);
		}
		holder.tv.setText(myFile.file.getName());
		return view;
	}
	
	static class ViewHolder{
		TextView tv;
		ImageView iv;
	}

}
