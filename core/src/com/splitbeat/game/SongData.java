package com.splitbeat.game;

import com.badlogic.gdx.maps.tiled.TiledMap;

public class SongData{
	
	//Folder name
	private String mName;
	
	//Title to be displayed
	private String mTitle;
	
	private String mArtist;
	private float mBpm;
	private int mLengthSeconds;
	private float mOffset;
	
	private TiledMap mEasyLeftMap;
	private TiledMap mEasyRightMap;
	private TiledMap mMediumLeftMap;
	private TiledMap mMediumRightMap;
	private TiledMap mHardLeftMap;
	private TiledMap mHardRightMap;
	
	SongData(String name){
		mName = name;
		mTitle = "";
		mArtist = "";
		mBpm = 0.f;
		mLengthSeconds = 0;
		mOffset = 0.f;
		mEasyLeftMap = null;
		mEasyRightMap = null;
		mMediumLeftMap = null;
		mMediumRightMap = null;
		mHardLeftMap = null;
		mHardRightMap = null;
	}
	
	public void setEasyMaps(TiledMap left, TiledMap right){
		mEasyLeftMap = left;
		mEasyRightMap = right;
	}
	
	public void setMediumMaps(TiledMap left, TiledMap right){
		mMediumLeftMap = left;
		mMediumRightMap = right;
	}
	
	public void setHardMaps(TiledMap left, TiledMap right){
		mHardLeftMap = left;
		mHardRightMap = right;
	}
	
	public TiledMap getLeftMap(Difficulty difficulty){
		switch(difficulty){
		case Easy:
			return mEasyLeftMap;
		case Medium:
			return mMediumLeftMap;
		case Hard:
			return mHardLeftMap;
		default:
			return mEasyLeftMap;
		}
	}
	
	public TiledMap getRightMap(Difficulty difficulty){
		switch(difficulty){
		case Easy:
			return mEasyRightMap;
		case Medium:
			return mMediumRightMap;
		case Hard:
			return mHardRightMap;
		default:
			return mEasyRightMap;
		}
	}
	
	public String getName(){ return mName; }
	public void setTitle(String title){ mTitle = title; }
	public String getTitle(){ return mTitle; }
	public void setArtist(String artist){ mArtist = artist; }
	public String getArtist(){ return mArtist; }
	public void setBpm(float bpm){ mBpm = bpm; }
	public float getBpm(){return mBpm; }
	public void setLength(int seconds){ mLengthSeconds = seconds; }
	public int getLength(){ return mLengthSeconds; }
	public void setOffset(float offset){ mOffset = offset; }
	public float getOffset(){ return mOffset; }
	
}
