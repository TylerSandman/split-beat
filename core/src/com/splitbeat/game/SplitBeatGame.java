package com.splitbeat.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;

public class SplitBeatGame extends Game {
	
	@Override
	public void create(){
		Assets.instance.init(new AssetManager());
		setScreen(new GameScreen(this));
	}
}
