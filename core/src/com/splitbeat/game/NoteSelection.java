package com.splitbeat.game;

import java.util.ArrayList;

public class NoteSelection {
	
	private float mStartBeat;
	private float mEndBeat;
	private boolean mOpen;
	private boolean mActive;
	
	NoteSelection(){
		init();
	}
	
	private void init(){
		mStartBeat = 0.f;
		mEndBeat = 0.f;
		mOpen = false;	
		mActive = false;
	}
	
	public float getStartBeat(){
		return mStartBeat;
	}
	
	public void open(float startBeat){
		mStartBeat = startBeat;
		mEndBeat = startBeat;
		mOpen = true;
		mActive = true;
	}
	
	public float getEndBeat(){
		return mEndBeat;
	}
	
	public void close(float endBeat){
		mEndBeat = endBeat;
		
		//Switch start and end if start beat is larger
		if (mEndBeat < mStartBeat){
			float temp = mStartBeat;
			mStartBeat = mEndBeat;
			mEndBeat = temp;
		}
		mOpen = false;
	}
	
	public ArrayList<Note> selectNotes(ArrayList<Note> notes){
		
		ArrayList<Note> selection = new ArrayList<Note>();
		if (isOpen()) return selection;
		for (Note note : notes){
			if (note.beat >= mStartBeat && note.beat <= mEndBeat)
				selection.add(note);
		}
		return selection;
	}
	
	public ArrayList<HoldNote> selectHoldNotes(ArrayList<HoldNote> notes){
		
		ArrayList<HoldNote> selection = new ArrayList<HoldNote>();
		if (isOpen()) return selection;
		for (HoldNote hold : notes){
			if (hold.beat >= mStartBeat && hold.beat <= mEndBeat)
				selection.add(hold);
		}
		return selection;
	}
	
	public boolean isOpen(){
		return mOpen;
	}
	
	public boolean isActive(){
		return mActive;
	}
	
	public void clear(){
		init();
	}
}