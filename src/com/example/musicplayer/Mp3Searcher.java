package com.example.musicplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class Mp3Searcher {
	  private final String tag="Mp3Searcher";
	       private ArrayList<Map<String, String>> musicFileList;
	    
	      public Mp3Searcher() {
	           musicFileList = new ArrayList<Map<String, String>>();
	       }
	    
	       /**
	        * 此方法使用递归方式将搜索到的mp3文件信息添加到一个list中
	        * @param dir 从哪个路径下开始查找mp3
	        */
	      private void getMp3InDir(File dir) {
	          if (dir.isDirectory()) {
	               File[] files = dir.listFiles();
	               for (File f : files) {
	                  getMp3InDir(f);
	               }
	           } else {
	               if (dir.isFile() && dir.getName().endsWith(".mp3")) {
	                   HashMap<String, String> fileInfoMap = new HashMap<String, String>();
	                   fileInfoMap.put("fileName", dir.getName());
	                   fileInfoMap.put("filePosition", dir.getAbsolutePath());
	                   musicFileList.add(fileInfoMap);
	              }
	           }
	       }
	    
	       /**
	        * 
	       * @param dir 要查找mp3的目录,递归查找
	        * @return 返回一个list,这个list中包含了所有查找到的mp3文件,如果list为空,则表示没有搜索到mp3
	       */
	      public ArrayList<Map<String, String>> getAllMp3Files(File dir) {
	           Log.v(tag, "getAllMp3Files");
	          this.musicFileList.clear();
	           this.getMp3InDir(dir);
	          return this.musicFileList;
	       }
	    
	       /**
	        * 打印mp3搜索结果
	        */
	      public void printMusicFileList() {
	           if (this.musicFileList.isEmpty()) {
	               System.out.println("empty list");
	           } else {
	               for (Map<String, String> fileInfomap : this.musicFileList) {
	                   System.out.println("fileName: " + fileInfomap.get("fileName"));
	                   System.out.println("filePosition: "
	                           + fileInfomap.get("filePosition"));
	               }
	           }
	       }
	    
	   }
