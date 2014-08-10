package com.splitbeat.game;

import java.io.File;
import java.io.IOException;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
public class SongEditScreen extends AbstractGameScreen {
	
	private TmxMapBuilder mBuilder;
	
	SongEditScreen(Game game){
		super(game);
	}
	
	private void init(){
		mBuilder = new TmxMapBuilder();
		SongData data = new SongData("Test Map");
		data.setArtist("Tyler.S");
		data.setBpm(170.f);
		data.setTitle("Test Map");
		data.setOffset(-0.04f);
		data.setLength(220);
		mBuilder.create(data);
	}
	
	@Override
	public void show() {
		init();
	}

	@Override
	public void hide() {}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	@Override
	public void resize(int width, int height) {}

	@Override
	public void pause() {}

}