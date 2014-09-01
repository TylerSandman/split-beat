package com.splitbeat.game;

public class Constants {
	
	public static final String TEXTURE_ATLAS_NOTES = 
			"images/game/split-beat-game.pack";
	
	public static final String TEXTURE_ATLAS_GUI = 
			"images/gui/split-beat-gui.pack";
	
	public static final String GUI_SKIN = 
			"images/gui/split-beat-gui.json";
	
	public static enum ButtonColor{
		RED,
		BLUE,
		GREEN,
		PURPLE,
		YELLOW
	}
	
	public static enum NoteType{
		QUARTER,
		EIGHTH,
		TWELVTH,
		SIXTEENTH,
		TWENTY_FOURTH,
		THIRTY_SECOND,
		OUTLINE;
		
		public String toString(){
			switch(this){
			case QUARTER:
				return "quarter";
			case EIGHTH:
				return "eighth";
			case TWELVTH:
				return "twelvth";
			case SIXTEENTH:
				return "sixteenth";
			case TWENTY_FOURTH:
				return "twenty_fourth";
			case THIRTY_SECOND:
				return "thirty_second";
			default:
				return "quarter";
			}
		}
		
		public static NoteType stringToType(String str){
			
			if (str.equals("quarter"))
				return NoteType.QUARTER;
			if (str.equals("eighth"))
				return NoteType.EIGHTH;
			if (str.equals("twelvth"))
				return NoteType.TWELVTH;
			if (str.equals("sixteenth"))
				return NoteType.SIXTEENTH;
			if (str.equals("twenty_fourth"))
				return NoteType.TWENTY_FOURTH;
			if (str.equals("thirty_second"))
				return NoteType.THIRTY_SECOND;
			return NoteType.QUARTER;
		}
	}
	
	public static enum NoteSlot{
		TOP_LEFT,
		MIDDLE_LEFT,
		BOTTOM_LEFT,
		TOP_RIGHT,
		MIDDLE_RIGHT,
		BOTTOM_RIGHT;
		
		public String toString(){
			
			switch(this){
			case TOP_LEFT:
				return "top_left";
			case MIDDLE_LEFT:
				return "middle_left";
			case BOTTOM_LEFT:
				return "bottom_left";
			case TOP_RIGHT:
				return "top_right";
			case MIDDLE_RIGHT:
				return "middle_right";
			case BOTTOM_RIGHT:
				return "bottom_right";
			default:
				return "top_left";
			}
		}
		
		public static NoteSlot stringToSlot(String str){
			
			if (str.equals("top_left"))
				return NoteSlot.TOP_LEFT;
			if (str.equals("middle_left"))
				return NoteSlot.MIDDLE_LEFT;
			if (str.equals("bottom_left"))
				return NoteSlot.BOTTOM_LEFT;
			if (str.equals("top_right"))
				return NoteSlot.TOP_RIGHT;
			if (str.equals("middle_right"))
				return NoteSlot.MIDDLE_RIGHT;
			if (str.equals("bottom_right"))
				return NoteSlot.BOTTOM_RIGHT;
			return NoteSlot.TOP_LEFT;
		}
	}
	
	public static enum Timing{
		MISS,
		GOOD,
		GREAT,
		AMAZING,
		FLAWLESS,
		HOLD,
		DROP,
		NONE
	}
	
	//File paths
	public static final String SYNC_LEFT_MAP = "maps/sync_left.tmx";
	public static final String SYNC_RIGHT_MAP = "maps/sync_right.tmx";
	public static final String DEFAULT_MAPS_PATH = "./Songs/";
	
	//Tiled spritesheet GIDs
	public static final int RED_ID = 8;
	public static final int BLUE_ID = 6;
	public static final int GREEN_ID = 1;
	public static final int PURPLE_ID = 2;
	
	//In seconds
	public static final float MISS_WINDOW = 0.160f;
	public static final float GOOD_WINDOW = 0.135f;
	public static final float GREAT_WINDOW = 0.102f;
	public static final float AMAZING_WINDOW = 0.043f;
	public static final float FLAWLESS_WINDOW = 0.0200f;
	public static final float HOLD_WINDOW = 0.1f;
	public static final float NO_WINDOW = -1f;
	public static final float SYNC_BPM = 120.f;
	
	//Timing window point system
	public static final int FLAWLESS_POINTS = 5;
	public static final int AMAZING_POINTS = 4;
	public static final int GREAT_POINTS = 2;
	public static final int GOOD_POINTS = 0;
	public static final int MISS_POINTS = -9;
	public static final int HOLD_POINTS = 5;
	public static final int DROP_POINTS = 0;
	
	//Conversion and measurement constants
	public static final float CELL_PADDING = 10.f;
	public static final int MEASURE_WIDTH_NOTES = 4;
	public static final int NUM_SYNC_CALCULATION_BEATS = 4;
	public static final float GLOW_ANIMATION_TIME = 0.1f;
	public static final float OUTLINE_PRESS_ANIMATION_TIME = 0.1f;
	public static final float SYNC_BEAT_DIFFERENCE_THRESHOLD = 0.1f;
	public static final float MAX_ZOOM = 3.0f;
	public static final float MIN_ZOOM = 1.0f;
	public static final float ZOOM_INCREMENT = 0.1f;
	public static final int NUM_REDOS = 50;
}
