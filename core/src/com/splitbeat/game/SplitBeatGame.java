package com.splitbeat.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;

public class SplitBeatGame extends Game {
	
	@Override
	public void create(){
		Options.instance.init();
		Assets.instance.init(new AssetManager());
		AudioManager.instance.init();		
		setScreen(new GameScreen(this, 0, Difficulty.Easy));
		setScreen(new MenuScreen(this));
	}
	
	@Override
	public void dispose(){
		Assets.instance.dispose();
		Options.instance.dispose();
	}
}
