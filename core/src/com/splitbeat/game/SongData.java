package com.splitbeat.game;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class SongData implements Json.Serializable {
	
	private String mName;
	private String mArtist;
	private String mFilename;
	private float mBpm;
	private float mLengthSeconds;
	private float mEasyScore;
	private float mMediumScore;
	private float mHardScore;
	
	SongData(){}
	
	SongData(String name, String artist, String filename,
			float bpm, float lengthSeconds, float easyScore, float mediumScore, float hardScore){
		mName = name;
		mArtist = artist;
		mFilename = filename;
		mBpm = bpm;
		mLengthSeconds = lengthSeconds;
		mEasyScore = easyScore;
		mMediumScore = mediumScore;
		mHardScore = hardScore;
	}
	
	public String getName(){ return mName; }
	public String getArtist(){ return mArtist; }
	public String getFilename(){ return mFilename; }
	public float getBPM(){ return mBpm; }
	public float getLength(){ return mLengthSeconds; }
	public float getEasyScore(){ return mEasyScore; }
	public float getMediumScore(){ return mMediumScore; }
	public float getHardScore(){ return mHardScore; }
	
	private String getDifPath(Difficulty difficulty){
		String difficultyPath;
		switch(difficulty){
		case Easy:
			difficultyPath = Constants.EASY_PATH;
			break;
		case Medium:
			difficultyPath = Constants.MEDIUM_PATH;
			break;
		case Hard:
			difficultyPath = Constants.HARD_PATH;
			break;
		default:
			difficultyPath = Constants.EASY_PATH;
			break;
		}
		return difficultyPath;
	}
	
	public String getLeftPath(Difficulty difficulty){

		String rawName = mFilename.substring(0, mFilename.length() - 4);
		return getDifPath(difficulty) + rawName + "_left.tmx";
	}
	
	public String getRightPath(Difficulty difficulty){
		
		String rawName = mFilename.substring(0, mFilename.length() - 4);
		return getDifPath(difficulty) + rawName + "_right.tmx";
	}
	
	@Override
	public void write(Json json) {
		json.writeValue("name", mName, String.class);
		json.writeValue("artist", mArtist, String.class);
		
		//Have to remove periods in filepaths so LibGDX's JSON pattern
		//matcher properly quotes it as a string
		json.writeValue("filename", mFilename.replace(".", " "), String.class);
		
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
		mFilename = jsonData.getString("filename", "").replace(" ", ".");
		mBpm = jsonData.getFloat("bpm", 0.f);
		mLengthSeconds = jsonData.getFloat("length", 0.f);
		mEasyScore = jsonData.getFloat("easy_score", 0.f);
		mMediumScore = jsonData.getFloat("medium_score", 0.f);
		mHardScore = jsonData.getFloat("hard_score", 0.f);
	}
}
