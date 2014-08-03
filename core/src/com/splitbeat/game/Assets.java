package com.splitbeat.game;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;

public class Assets implements Disposable, AssetErrorListener{
	
	public static final Assets instance = new Assets();
	private AssetManager mAssetManager;
	public AssetGUI gui;
	public AssetButton button;
	public AssetMap maps;
	public AssetMusic music;
	public AssetSounds sounds;
	public AssetFonts fonts;
	public AssetData data;
	
	private Assets(){}
	
	public void init (AssetManager assetManager){
		
		//Resolution smallRes = new Resolution(320, 480, "480x320");
		//Resolution largeRes = new Resolution(760, 920, "960x720");
		//Resolution[] resolutions = new Resolution[]{ smallRes, largeRes };
		//ResolutionFileResolver resolver = new ResolutionFileResolver(new InternalFileHandleResolver(), resolutions);
		
		mAssetManager = assetManager;
		mAssetManager.setErrorListener(this);	
		
		//Load level
		mAssetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
		mAssetManager.load(Constants.LEFT_MAPS[0], TiledMap.class);
		mAssetManager.load(Constants.RIGHT_MAPS[0], TiledMap.class);
		mAssetManager.load(Constants.LEFT_MAPS[1], TiledMap.class);
		mAssetManager.load(Constants.RIGHT_MAPS[1], TiledMap.class);
		mAssetManager.finishLoading();
		
		//Load button textures;
		mAssetManager.setLoader(TextureAtlas.class, new TextureAtlasLoader(new InternalFileHandleResolver()));
		mAssetManager.load(Constants.TEXTURE_ATLAS_NOTES, TextureAtlas.class);
		mAssetManager.finishLoading();
		
		//Load GUI
		mAssetManager.load(Constants.TEXTURE_ATLAS_GUI, TextureAtlas.class);
		mAssetManager.finishLoading();
		
		//Load music
		mAssetManager.load("music/paper_planes.ogg", Music.class);
		mAssetManager.load("music/sync.ogg", Music.class);
		mAssetManager.finishLoading();
		
		//Load sound
		mAssetManager.load("sound/sync_click.ogg", Sound.class);
		mAssetManager.finishLoading();
		
		//Initialize assets
		TextureAtlas atlas = mAssetManager.get(Constants.TEXTURE_ATLAS_NOTES);	
		button = new AssetButton(atlas);
		maps = new AssetMap(mAssetManager);
		music = new AssetMusic(mAssetManager);
		sounds = new AssetSounds(mAssetManager);
		fonts = new AssetFonts();
		data = new AssetData();
		atlas = mAssetManager.get(Constants.TEXTURE_ATLAS_GUI);
		gui = new AssetGUI(atlas);	
	}

	@Override
	public void error(AssetDescriptor asset, Throwable throwable) {
		Gdx.app.error("Asset", "Couldn't load asset '" + asset.fileName + "'", (Exception) throwable);
	}

	@Override
	public void dispose() {	
		mAssetManager.dispose();
		fonts.defaultFont.dispose();
		music.paperPlanes.dispose();
	}
	
	public class AssetGUI{
		
		public final AtlasRegion repeatGradient;
		public final AtlasRegion repeatGradientHighlight;
		AssetGUI(TextureAtlas atlas){
			repeatGradient = atlas.findRegion("gradient_repeat");
			repeatGradientHighlight = atlas.findRegion("gradient_repeat_highlight");
		}
	}
	
	public class AssetButton{

		public final AtlasRegion buttonRed;
		public final AtlasRegion buttonBlue;
		public final AtlasRegion buttonGreen;
		public final AtlasRegion buttonOrange;
		public final AtlasRegion buttonYellow;
		public final AtlasRegion buttonGrey;
		public final AtlasRegion holdOverlay;
		public final AtlasRegion holdBackground;
		public final AtlasRegion hitOverlay;
		public final AtlasRegion hitBackground;
		AssetButton(TextureAtlas atlas){
			
			String filename = "Magic_button_";
			buttonRed = atlas.findRegion(filename + "red");
			buttonBlue = atlas.findRegion(filename + "blue");
			buttonGreen = atlas.findRegion(filename + "green");
			buttonOrange = atlas.findRegion(filename + "orange");
			buttonYellow = atlas.findRegion(filename + "yellow");
			buttonGrey = atlas.findRegion(filename + "grey");
			holdOverlay = atlas.findRegion("hold_overlay");
			holdBackground = atlas.findRegion("hold_background");
			hitOverlay = atlas.findRegion("hit_overlay");
			hitBackground = atlas.findRegion("hit_background");
		}
	}
	
	public class AssetMusic{
		
		public final Music paperPlanes;
		public final Music sync;
		AssetMusic(AssetManager am){
			paperPlanes = am.get("music/paper_planes.ogg", Music.class);
			sync = am.get("music/sync.ogg", Music.class);
		}	
	}
	
	public class AssetSounds{
		
		public final Sound syncClick;
		AssetSounds(AssetManager am){
			syncClick = am.get("sound/sync_click.ogg", Sound.class);
		}
	}
	
	public class AssetMap{
		
		public final HashMap<String, TiledMap> leftMaps;
		public final HashMap<String, TiledMap> rightMaps;
		AssetMap(AssetManager am){
			leftMaps = new HashMap<String, TiledMap>();
			rightMaps = new HashMap<String, TiledMap>();
			leftMaps.put(Constants.LEFT_MAPS[0], am.get(Constants.LEFT_MAPS[0], TiledMap.class));
			rightMaps.put(Constants.RIGHT_MAPS[0], am.get(Constants.RIGHT_MAPS[0], TiledMap.class));
			leftMaps.put(Constants.LEFT_MAPS[1], am.get(Constants.LEFT_MAPS[1], TiledMap.class));
			rightMaps.put(Constants.RIGHT_MAPS[1], am.get(Constants.RIGHT_MAPS[1], TiledMap.class));
		}
	}
	
	public class AssetFonts{
		
		public final BitmapFont defaultFont;
		AssetFonts(){
			defaultFont = new BitmapFont();
			defaultFont.setUseIntegerPositions(false);
		}
	}
	
	public class AssetData{
		
		public class SongData{
			public final float bpm;
			public final float lengthSeconds;
			public final String name;
			public final String artist;
			SongData(float bpm, float lengthSeconds, String name, String artist){
				this.bpm = bpm;
				this.lengthSeconds = lengthSeconds;
				this.name = name;
				this.artist = artist;
			}
		}
		
		public final ArrayList<SongData> songs;
		AssetData(){			
			songs = new ArrayList<SongData>();
			songs.add(new SongData(170.0f, 220.f, "Paper Planes", "Virtual Riot"));
			songs.add(new SongData(170.0f, 220.f, "Paper Planes", "Virtual Riot"));
			songs.add(new SongData(170.0f, 220.f, "Paper Planes", "Virtual Riot"));
			songs.add(new SongData(170.0f, 220.f, "Paper Planes", "Virtual Riot"));
			songs.add(new SongData(170.0f, 220.f, "Paper Planes", "Virtual Riot"));
			songs.add(new SongData(170.0f, 220.f, "Paper Planes", "Virtual Riot"));
			songs.add(new SongData(170.0f, 220.f, "Paper Planes", "Virtual Riot"));
			songs.add(new SongData(170.0f, 220.f, "Paper Planes", "Virtual Riot"));
			songs.add(new SongData(170.0f, 220.f, "Paper Planes", "Virtual Riot"));
			songs.add(new SongData(170.0f, 220.f, "Paper Planes", "Virtual Riot"));
			songs.add(new SongData(170.0f, 220.f, "Paper Planes", "Virtual Riot"));
			songs.add(new SongData(170.0f, 220.f, "Paper Planes", "Virtual Riot"));
			songs.add(new SongData(170.0f, 220.f, "Paper Planes", "Virtual Riot"));
		}
	}

}
