package com.splitbeat.game;

import java.util.Comparator;

public class NoteComparator implements Comparator<Note> {

	@Override
	public int compare(Note o1, Note o2) {
		if (o1.beat < o2.beat) return -1;
		else if (o1.beat > o2.beat) return 1;
		else return 0;
	}
}
