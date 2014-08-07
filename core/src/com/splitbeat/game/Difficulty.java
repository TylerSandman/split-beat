package com.splitbeat.game;

public enum Difficulty{
	Easy,
	Medium,
	Hard;
	
	public Difficulty prev(){
		
		int indexWrapped = (this.ordinal() + Difficulty.values().length - 1) %
                Difficulty.values().length;
		return Difficulty.values()[indexWrapped];
	}
	
	public Difficulty next(){
		
		int indexWrapped = (this.ordinal() + 1) % Difficulty.values().length;
		return Difficulty.values()[indexWrapped];
	}
}
