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
	private String mMapsPath;
	
	private Options(){}
	
	public void init(){
		
		mPrefs = Gdx.app.getPreferences("config.sb");
		mOffset = mPrefs.getFloat("offset", 0.f);
		mMapsPath = mPrefs.getString("maps_path", Constants.DEFAULT_MAPS_PATH);
		initScores();
	}
	
	private void initScores(){
				
		Json json = new Json();
		String scoresStr = mPrefs.getString("scores", "");
		if (scoresStr.equals(""))
			mScores = new HashMap<String, SongScore>();
		else
			mScores = json.fromJson(HashMap.class, SongData.class, scoresStr);
		
		//Initialize scores for new songs
		for (Map.Entry<String, SongData> entry : Assets.instance.maps.dataMap.entrySet()){
			
			String name = entry.getKey();
			if (mScores.get(name) == null)
				mScores.put(name, new SongScore());		
		}
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
	
	public void setMapsPath(String path){
		mMapsPath = path;
		mPrefs.putString("maps_path", path);
	}
	
	public String getMapsPath(){
		return mMapsPath;
	}
	
	public SongScore getScores(String name){ return mScores.get(name); }

}
