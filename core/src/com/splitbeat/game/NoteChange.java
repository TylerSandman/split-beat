package com.splitbeat.game;

import java.util.ArrayList;

public interface NoteChange {
	
	public enum Type{
		Add,
		Remove
	};
	
	public Type getType();
	public ArrayList<Note> getRegularNotes();
	public ArrayList<HoldNote> getHoldNotes();
}
