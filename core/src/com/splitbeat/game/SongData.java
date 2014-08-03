package com.splitbeat.game;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class SongData implements Json.Serializable {
	
	private String mName;
	private String mArtist;
	private String mLeftFilePath;
	private String mRightFilePath;
	private float mBpm;
	private float mLengthSeconds;
	private float mEasyScore;
	private float mMediumScore;
	private float mHardScore;
	
	SongData(){}
	
	SongData(String name, String artist, String leftFilePath, String rightFilePath,
			float bpm, float lengthSeconds, float easyScore, float mediumScore, float hardScore){
		mName = name;
		mArtist = artist;
		mLeftFilePath = leftFilePath;
		mRightFilePath = rightFilePath;
		mBpm = bpm;
		mLengthSeconds = lengthSeconds;
		mEasyScore = easyScore;
		mMediumScore = mediumScore;
		mHardScore = hardScore;
	}
	
	public String getName(){ return mName; }
	public String getArtist(){ return mArtist; }
	public String getLeftPath(){ return mLeftFilePath; }
	public String getRightPath(){ return mRightFilePath; }
	public float getBPM(){ return mBpm; }
	public float getLength(){ return mLengthSeconds; }
	public float getEasyScore(){ return mEasyScore; }
	public float getMediumScore(){ return mMediumScore; }
	public float getHardScore(){ return mHardScore; }
	
	@Override
	public void write(Json json) {
		json.writeValue("name", mName, String.class);
		json.writeValue("artist", mArtist, String.class);
		json.writeValue("left_path", mLeftFilePath.replace(".", " "), String.class);
		json.writeValue("right_path", mRightFilePath.replace(".", " "), String.class);
		json.writeValue("bpm", mBpm, Float.class);
		json.writeValue("length", mLengthSeconds, Float.class);
		json.writeValue("easy_score", mEasyScore, Float.class);
		json.writeValue("medium_score", mMediumScore, Float.class);
		json.writeValue("hard_score", mHardScore, Float.class);
	}
	
	@Override
	public void read(Json json, JsonValue jsonData) {
		mName = jsonData.getString("name", "");
		mArtist = jsonData.getString("artist", "");
		mLeftFilePath = jsonData.getString("left_path", "").replace(" ", ".");
		mRightFilePath = jsonData.getString("right_path", "").replace(" ", ".");
		mBpm = jsonData.getFloat("bpm", 0.f);
		mLengthSeconds = jsonData.getFloat("length", 0.f);
		mEasyScore = jsonData.getFloat("easy_score", 0.f);
		mMediumScore = jsonData.getFloat("medium_score", 0.f);
		mHardScore = jsonData.getFloat("hard_score", 0.f);
	}
}
