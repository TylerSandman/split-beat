package com.splitbeat.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;

public class Options implements Disposable{
	
	public static final Options instance = new Options();
	
	private Preferences mPrefs;
	public float offset;
	public ArrayList<SongData> songsData;
	
	private Options(){}
	
	public void init(){
		
		mPrefs = Gdx.app.getPreferences("config.sb");
		offset = mPrefs.getFloat("offset", 0.f);
		
		Json json = new Json();
		String songDataString = mPrefs.getString("songs_data", "");
		if (songDataString.equals(""))
			initSongsData();
		else{
			songsData = json.fromJson(ArrayList.class, SongData.class, songDataString);
		}
	}
	
	private void initSongsData(){
		
		songsData = new ArrayList<SongData>();
		songsData.add(new SongData("Paper Planes", "Virtual Riot", "maps/testEasyLeft.tmx", "maps/testEasy.tmx",
				170.f, 220.f, 0.f, 0.f, 0.f));
		songsData.add(new SongData("Paper Planes", "Virtual Riot", "maps/testEasyLeft.tmx", "maps/testEasy.tmx",
				170.f, 220.f, 0.f, 0.f, 0.f));
		songsData.add(new SongData("Paper Planes", "Virtual Riot", "maps/testEasyLeft.tmx", "maps/testEasy.tmx",
				170.f, 220.f, 0.f, 0.f, 0.f));
		songsData.add(new SongData("Paper Planes", "Virtual Riot", "maps/testEasyLeft.tmx", "maps/testEasy.tmx",
				170.f, 220.f, 0.f, 0.f, 0.f));
		songsData.add(new SongData("Paper Planes", "Virtual Riot", "maps/testEasyLeft.tmx", "maps/testEasy.tmx",
				170.f, 220.f, 0.f, 0.f, 0.f));
		songsData.add(new SongData("Paper Planes", "Virtual Riot", "maps/testEasyLeft.tmx", "maps/testEasy.tmx",
				170.f, 220.f, 0.f, 0.f, 0.f));
		songsData.add(new SongData("Paper Planes", "Virtual Riot", "maps/testEasyLeft.tmx", "maps/testEasy.tmx",
				170.f, 220.f, 0.f, 0.f, 0.f));
		songsData.add(new SongData("Paper Planes", "Virtual Riot", "maps/testEasyLeft.tmx", "maps/testEasy.tmx",
				170.f, 220.f, 0.f, 0.f, 0.f));
		songsData.add(new SongData("Paper Planes", "Virtual Riot", "maps/testEasyLeft.tmx", "maps/testEasy.tmx",
				170.f, 220.f, 0.f, 0.f, 0.f));
		songsData.add(new SongData("Paper Planes", "Virtual Riot", "maps/testEasyLeft.tmx", "maps/testEasy.tmx",
				170.f, 220.f, 0.f, 0.f, 0.f));
		Json json = new Json();
		mPrefs.putString("songs_data", json.toJson(songsData, Array.class, SongData.class));
	}

	@Override
	public void dispose() {
		mPrefs.flush();
	}
	
	public void setOffset(float newOffset){
		offset = newOffset;
		mPrefs.putFloat("offset", offset);
	}

}
