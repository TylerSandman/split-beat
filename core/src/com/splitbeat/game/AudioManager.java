package com.splitbeat.game;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioManager {
	
	public static final AudioManager instance = new AudioManager();
	
	private Music mPlayingMusic;
	
	private AudioManager(){}
	
	public void play(Sound sound){
		play(sound, 1);
	}
	
	public void play(Sound sound, float volume){
		play(sound, volume, 1);
	}
	
	public void play(Sound sound, float volume, float pitch){
		play(sound, volume, pitch, 0);
	}
	
	public void play(Sound sound, float volume, float pitch, float pan){
		sound.play(volume, pitch, pan);
	}
	
	public void play(Music music){
		stopMusic();
		mPlayingMusic = music;
		mPlayingMusic.setLooping(true);
		mPlayingMusic.play();
	}
	
	public void stopMusic(){
		if (mPlayingMusic != null)
			mPlayingMusic.stop();
	}
}
