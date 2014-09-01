package com.splitbeat.game;

import java.util.ArrayList;

import com.splitbeat.game.NoteChange.Type;

public class RemoveNoteChange implements NoteChange{
	
	private ArrayList<Note> mAffectedRegularNotes;
	private ArrayList<HoldNote> mAffectedHoldNotes;
	
	RemoveNoteChange(ArrayList<Note> regularNotes, ArrayList<HoldNote> holdNotes){
		mAffectedRegularNotes = regularNotes;
		mAffectedHoldNotes = holdNotes;
	}

	@Override
	public Type getType() {
		return Type.Remove;
	}

	@Override
	public ArrayList<Note> getRegularNotes() {
		return mAffectedRegularNotes;
	}

	@Override
	public ArrayList<HoldNote> getHoldNotes() {
		return mAffectedHoldNotes;
	}
}
