package com.splitbeat.game;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;

public class MarkerFactory {
	
	public static BPMMarker createMarker(MapObject mObject, TiledMap map, boolean leftTrack){
		
		int mapWidth = map.getProperties().get("width", Integer.class);
		int tileWidth = map.getProperties().get("tilewidth", Integer.class);
		
		String beatStr = mObject.getProperties().get("beat", String.class);
		float beat = Float.parseFloat(beatStr);
		String bpmStr= mObject.getProperties().get("bpm", String.class);
		float bpm = Float.parseFloat(bpmStr);
		
		return new BPMMarker(beat, bpm, leftTrack);
	}
}
