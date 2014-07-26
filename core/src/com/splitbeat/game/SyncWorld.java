package com.splitbeat.game;

import com.badlogic.gdx.Game;

public class SyncWorld extends World {

	private float mSecondsSincePlay;
	
	//Marker so we don't play the sync click twice in one beat
	//due to low deltaTime values
	private boolean mWaitingForBeat;
	
	SyncWorld(Game game) {
		super(game);
		mSecondsSincePlay = 0.f;
		mWaitingForBeat = true;
	}
	
	@Override
	protected void initMusic(){}
	
	@Override
	protected void update(float deltaTime){
		super.updateSong(deltaTime);
		mSecondsSincePlay += deltaTime;
		float secondsFrombeat = mSecondsSincePlay % (1 / (Constants.SYNC_BPM / 60.f));
		if ((Math.abs(secondsFrombeat) < (1.f / 60.f)) && (mWaitingForBeat)){
			AudioManager.instance.play(Assets.instance.sounds.syncClick);
			mWaitingForBeat = false;
			return;
		}
		mWaitingForBeat = true;
	}

}
