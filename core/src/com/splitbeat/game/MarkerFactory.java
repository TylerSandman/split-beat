package com.splitbeat.game;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;

public class MarkerFactory {
	
	public static BPMMarker createMarker(MapObject mObject, TiledMap map, boolean leftTrack){
		
		int mapWidth = map.getProperties().get("width", Integer.class);
		int tileWidth = map.getProperties().get("tilewidth", Integer.class);
		
		float notePos = mObject.getProperties().get("x", Float.class);
		float beat = (mapWidth - (notePos / tileWidth) - 1) / 4.f;
		String bpmStr= mObject.getProperties().get("bpm", String.class);
		float bpm = Float.parseFloat(bpmStr);
		
		return new BPMMarker(beat, bpm, leftTrack);
	}
}
