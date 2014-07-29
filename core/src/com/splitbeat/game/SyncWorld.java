package com.splitbeat.game;

import com.badlogic.gdx.Game;

public class SyncWorld extends World {

	private float mSecondsSincePlay;
	
	//Marker so we don't play the sync click twice in one beat
	//due to low deltaTime values
	private boolean mWaitingForBeat;
	
	SyncWorld(Game game) {
		super(game, 1);
	}
	
	@Override
	protected void initMusic(){
		AudioManager.instance.play(Assets.instance.music.sync);
	}
}
