package com.splitbeat.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;

public class SplitBeatGame extends ApplicationAdapter {
	
	private boolean mPaused;
	private World mWorld;
	
	@Override
	public void create (){
		Assets.instance.init(new AssetManager());
		mWorld = new World();	
		mPaused = false;
	}
	
	@Override
	public void pause(){
		mPaused = true;
	}
	
	@Override
	public void resume(){
		mPaused = false;
	}

	@Override
	public void render (){
		super.render();
		if (!mPaused)
			mWorld.update(Gdx.graphics.getDeltaTime());
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		mWorld.render();
	}
	
	@Override
	public void resize(int width, int height){
		mWorld.resize(width, height);
	}
	
	@Override
	public void dispose(){
		mWorld.dispose();
		Assets.instance.dispose();
	}
}
