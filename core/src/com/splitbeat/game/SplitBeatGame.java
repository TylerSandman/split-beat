package com.splitbeat.game;

import java.util.Iterator;
import java.util.Map.Entry;

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
		Iterator<Entry<String, SongData>> it = Assets.instance.maps.dataMap.entrySet().iterator();
		String songName = it.next().getKey();
		Difficulty difficulty  = Difficulty.Hard;
		while ((Assets.instance.maps.dataMap.get(songName).getLeftMap(Difficulty.Easy) == null) &&
			   (Assets.instance.maps.dataMap.get(songName).getLeftMap(Difficulty.Medium) == null) &&
			   (Assets.instance.maps.dataMap.get(songName).getLeftMap(Difficulty.Hard) == null)){
			songName = it.next().getKey();
		}
		for (Difficulty dif : Difficulty.values()){
			if (Assets.instance.maps.dataMap.get(songName).getLeftMap(dif) != null)
				difficulty = dif;
		}
		setScreen(new GameScreen(
				this, 
				songName, 
				difficulty));
		setScreen(new MenuScreen(this));
	}
	
	@Override
	public void dispose(){
		Assets.instance.dispose();
		Options.instance.dispose();
	}
}
