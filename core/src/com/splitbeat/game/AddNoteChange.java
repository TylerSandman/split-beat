package com.splitbeat.game;

import java.util.ArrayList;

public class AddNoteChange implements NoteChange {

	private ArrayList<Note> mAffectedRegularNotes;
	private ArrayList<HoldNote> mAffectedHoldNotes;
	
	AddNoteChange(ArrayList<Note> regularNotes, ArrayList<HoldNote> holdNotes){
		mAffectedRegularNotes = regularNotes;
		mAffectedHoldNotes = holdNotes;
	}

	@Override
	public Type getType() {
		return Type.Add;
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
