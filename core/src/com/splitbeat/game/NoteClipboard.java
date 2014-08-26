package com.splitbeat.game;

import java.util.ArrayList;
import java.util.Collections;

public class NoteClipboard {
	
	private ArrayList<Note> mNotes;
	private ArrayList<HoldNote> mHoldNotes;
	private float mFirstBeat;
	
	NoteClipboard(){}
	
	NoteClipboard(ArrayList<Note> notes, ArrayList<HoldNote> holds){
		mNotes = notes;
		mHoldNotes = holds;
	}
	
	public void clear(){
		mNotes = new ArrayList<Note>();
		mHoldNotes = new ArrayList<HoldNote>();
	}
	
	public ArrayList<Note> getNotes(){
		return mNotes;
	}
	
	public ArrayList<HoldNote> getHoldNotes(){
		return mHoldNotes;
	}
	
	public boolean isEmpty(){
		return (mNotes.size() == 0 &&  mHoldNotes.size() == 0);
	}
	
	public void copy(ArrayList<Note> notes, ArrayList<HoldNote> holds){
		mNotes = notes;
		mHoldNotes = holds;
		Collections.sort(notes, new NoteComparator());
		Collections.sort(holds, new NoteComparator());
		float beat1 = Float.MAX_VALUE;
		float beat2 = Float.MAX_VALUE;
		if (!notes.isEmpty()){
			beat1 = notes.get(0).beat;
		}
		if (!holds.isEmpty()){
			beat2 = holds.get(0).beat;
		}
	    mFirstBeat = Math.min(beat1, beat2);		
	}
	
	public float getFirstBeat(){
		return mFirstBeat;
	}
}
