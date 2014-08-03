package com.splitbeat.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;

public class SplitBeatGame extends Game {
	
	@Override
	public void create(){
		Assets.instance.init(new AssetManager());
		AudioManager.instance.init();
		Options.instance.init();
		setScreen(new GameScreen(this, 0));
		setScreen(new MenuScreen(this));
	}
	
	@Override
	public void dispose(){
		Assets.instance.dispose();
		Options.instance.dispose();
	}
}
