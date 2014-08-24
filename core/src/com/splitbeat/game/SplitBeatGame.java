package com.splitbeat.game;

import java.util.Map;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;

public class SplitBeatGame extends Game {
	
	@Override
	public void create(){
		Assets.instance.init(new AssetManager());
		Options.instance.init();
		AudioManager.instance.init();		
		
		//We are initially setting a game screen and destroying it
		//so the game stays in sync and all songs take a consistent
		//amount of time to load. Kind of hacky but it gets the job done.
		String songName = Assets.instance.maps.dataMap.entrySet().iterator().next().getKey();
		Difficulty difficulty  = Difficulty.Hard;
		for (Difficulty dif : Difficulty.values()){
			if (Assets.instance.maps.dataMap.get(songName).getLeftMap(dif) != null);
			difficulty = dif;
		}
		setScreen(new GameScreen(
				this, 
				(String) (Assets.instance.maps.dataMap.entrySet().iterator().next().getKey()), 
				difficulty));
		setScreen(new MenuScreen(this));
	}
	
	@Override
	public void dispose(){
		Assets.instance.dispose();
		Options.instance.dispose();
	}
}
