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
	
	private Options(){}
	
	public void init(){
		
		mPrefs = Gdx.app.getPreferences("config.sb");
		offset = mPrefs.getFloat("offset", 0.f);
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
