package com.splitbeat.game;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class SongScore implements Json.Serializable{
	
	private float mEasy;
	private float mMedium;
	private float mHard;
	
	SongScore(){
		mEasy = 0.f;
		mMedium = 0.f;
		mHard = 0.f;
	}

	SongScore(float easy, float medium, float hard){
		mEasy = easy;
		mMedium = medium;
		mHard = hard;
	}

	@Override
	public void write(Json json) {
		json.writeValue("easy", mEasy, Float.class);
		json.writeValue("medium", mMedium, Float.class);
		json.writeValue("hard", mHard, Float.class);	
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		mEasy = jsonData.getFloat("easy", 0.f);
		mMedium = jsonData.getFloat("medium", 0.f);
		mHard = jsonData.getFloat("hard", 0.f);
	}
	
	public float getEasy(){ return mEasy; }
	public float getMedium(){ return mMedium; }
	public float getHard(){ return mHard; }
}
