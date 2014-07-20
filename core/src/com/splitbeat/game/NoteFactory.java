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
		
		float notePos = nObject.getProperties().get("x", Float.class);
		float beat = (mapWidth - (notePos / tileWidth) - 1) / 4.f;
		float yPos = 3 * tileHeight - nObject.getProperties().get("y", Float.class);
		int slotPos = MathUtils.floor(yPos / tileHeight);
		int gID = nObject.getProperties().get("gid", Integer.class);
		NoteType type;
		NoteSlot slot;

		switch(slotPos){
		case 1:
			if (leftTrack)
				slot = NoteSlot.TOP_LEFT;
			else
				slot = NoteSlot.TOP_RIGHT;
			break;
		case 2:
			if (leftTrack)
				slot = NoteSlot.MIDDLE_LEFT;
			else
				slot = NoteSlot.MIDDLE_RIGHT;
			break;
		case 3:
			if (leftTrack)
				slot = NoteSlot.BOTTOM_LEFT;
			else
				slot = NoteSlot.BOTTOM_RIGHT;
			break;
		default:
			if (leftTrack)
				slot = NoteSlot.TOP_LEFT;
			else
				slot = NoteSlot.TOP_RIGHT;
			break;
		}
		
		switch(gID){
		case Constants.RED_ID:
			type = NoteType.QUARTER;
			break;
		case Constants.BLUE_ID:
			type = NoteType.EIGHTH;
			break;
		case Constants.GREEN_ID:
			type = NoteType.SIXTEENTH;
			break;
		case Constants.PURPLE_ID:
			type = NoteType.EIGHTH;
			break;
		default:
			type = NoteType.QUARTER;
			break;
		}
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
