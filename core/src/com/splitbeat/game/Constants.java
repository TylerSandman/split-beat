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
		OUTLINE
	}
	
	public static enum NoteSlot{
		TOP_LEFT,
		MIDDLE_LEFT,
		BOTTOM_LEFT,
		TOP_RIGHT,
		MIDDLE_RIGHT,
		BOTTOM_RIGHT
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
	
	public static final String SYNC_LEFT_MAP = "maps/syncLeft.tmx";
	public static final String SYNC_RIGHT_MAP = "maps/sync.tmx";
	
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
	public static final float BUTTON_PADDING = 10.f;
	public static final int MEASURE_WIDTH_NOTES = 4;
	public static final int NUM_SYNC_CALCULATION_BEATS = 4;
	public static final float GLOW_ANIMATION_TIME = 0.1f;
	public static final float OUTLINE_PRESS_ANIMATION_TIME = 0.1f;
	public static final float SYNC_BEAT_DIFFERENCE_THRESHOLD = 0.1f;
}
