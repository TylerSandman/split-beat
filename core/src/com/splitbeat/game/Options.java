package com.splitbeat.game;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;

public class Options implements Disposable{
	
	public static final Options instance = new Options();
	
	private Preferences mPrefs;
	private float mOffset;
	private Map<String, SongScore> mScores;
	
	private Options(){}
	
	public void init(){
		
		mPrefs = Gdx.app.getPreferences("config.sb");
		mOffset = mPrefs.getFloat("offset", 0.f);
		String scoresStr = mPrefs.getString("scores", "");
		if (scoresStr.equals("")){
			resetScores();
		}
		else{
			Json json = new Json();
			mScores = json.fromJson(HashMap.class, SongData.class, scoresStr);
		}
	}

	private void resetScores(){
		mScores = new HashMap<String, SongScore>();
		for (Map.Entry<String, SongData> entry : Assets.instance.maps.dataMap.entrySet()){
			
			String name = entry.getKey();
			mScores.put(name, new SongScore());		
		}
		Json json = new Json();	
		String jsonStr = json.toJson(mScores, HashMap.class, SongData.class);
		mPrefs.putString("scores", jsonStr);
	}
	
	public void updateScores(String name, String newName){
		
		SongScore scores = mScores.get(name);
		mScores.remove(name);
		mScores.put(newName, scores);
		
		Json json = new Json();	
		String jsonStr = json.toJson(mScores, HashMap.class, SongData.class);
		mPrefs.putString("scores", jsonStr);
	}

	@Override
	public void dispose() {
		mPrefs.flush();
	}
	
	public void setOffset(float newOffset){
		mOffset = newOffset;
		mPrefs.putFloat("offset", mOffset);
	}
	
	public float getOffset(){ return mOffset; }
	
	public SongScore getScores(String name){ return mScores.get(name); }

}
