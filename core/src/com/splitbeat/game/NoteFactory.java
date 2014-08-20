package com.splitbeat.game;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.splitbeat.game.Constants.NoteSlot;
import com.splitbeat.game.Constants.NoteType;

public class NoteFactory {
	
	public static Note createNote(MapObject nObject, TiledMap map, ScoreManager scoreManager, boolean leftTrack){
		
		int mapWidth = map.getProperties().get("width", Integer.class);
		int tileWidth = map.getProperties().get("tilewidth", Integer.class);
		int tileHeight = map.getProperties().get("tileheight", Integer.class);
		String bpmStr= map.getProperties().get("bpm", String.class);
		float bpm = Float.parseFloat(bpmStr);
		
		String beatStr = nObject.getProperties().get("beat", String.class);
		float beat = Float.parseFloat(beatStr);
		NoteSlot slot = NoteSlot.stringToSlot(nObject.getProperties().get("slot", String.class));
		NoteType type = NoteType.stringToType(nObject.getProperties().get("type", String.class));

		if (nObject.getProperties().get("hold") != null){
			String holdDurationStr = nObject.getProperties().get("hold", String.class);
			float holdDuration = (float) Double.parseDouble(holdDurationStr);
			return new HoldNote(beat, slot, type, holdDuration, bpm, scoreManager);
		}
		else{
			return new Note(beat, slot, type, scoreManager);
		}
	}
}
