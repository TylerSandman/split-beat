package com.splitbeat.game;

import java.util.ArrayList;

public class NoteClipboard {
	
	private ArrayList<Note> mNotes;
	
	NoteClipboard(){}
	
	NoteClipboard(ArrayList<Note> notes){
		mNotes = notes;
	}
	
	public void clear(){
		mNotes = new ArrayList<Note>();
	}
	
	public ArrayList<Note> getContents(){
		return mNotes;
	}
	
	public boolean isEmpty(){
		return (mNotes.size() > 0);
	}
	
	public void copy(ArrayList<Note> notes){
		mNotes = notes;
	}
	
	

}
