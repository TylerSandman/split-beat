package com.splitbeat.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class SyncGameScreen extends GameScreen {

	SyncGameScreen(Game game) {
		super(game, "", Difficulty.Easy);
	}
	
	@Override
	public void show() {
		mWorld = new SyncWorld(game);
		Gdx.input.setCatchBackKey(true);
	}

}
