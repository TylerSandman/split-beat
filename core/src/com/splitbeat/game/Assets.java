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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;

public class Assets implements Disposable, AssetErrorListener{
	
	public static final Assets instance = new Assets();
	private AssetManager mAssetManager;
	public AssetGUI gui;
	public AssetButton button;
	public AssetMap maps;
	public AssetMusic music;
	public AssetSounds sounds;
	public AssetFonts fonts;
	
	private Assets(){}
	
	public void init (AssetManager assetManager){
		
		//Resolution smallRes = new Resolution(320, 480, "480x320");
		//Resolution largeRes = new Resolution(760, 920, "960x720");
		//Resolution[] resolutions = new Resolution[]{ smallRes, largeRes };
		//ResolutionFileResolver resolver = new ResolutionFileResolver(new InternalFileHandleResolver(), resolutions);
		
		mAssetManager = assetManager;
		mAssetManager.setErrorListener(this);	
		
		//Load levels
		mAssetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
		for (SongData data : Options.instance.songsData){
			for (Difficulty dif : Difficulty.values()){
				mAssetManager.load(data.getLeftPath(dif), TiledMap.class);
				mAssetManager.load(data.getRightPath(dif), TiledMap.class);
			}
		}
		mAssetManager.load(Constants.SYNC_LEFT_MAP, TiledMap.class);
		mAssetManager.load(Constants.SYNC_RIGHT_MAP, TiledMap.class);
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
		public final AtlasRegion greenSlider;
		public final AtlasRegion yellowSlider;
		public final AtlasRegion redSlider;
		AssetGUI(TextureAtlas atlas){
			repeatGradient = atlas.findRegion("gradient_repeat");
			repeatGradientHighlight = atlas.findRegion("gradient_repeat_highlight");
			greenSlider = atlas.findRegion("green_sliderRight");
			yellowSlider = atlas.findRegion("yellow_sliderRight");
			redSlider = atlas.findRegion("red_sliderRight");
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
			for (SongData data : Options.instance.songsData){
				for (Difficulty dif : Difficulty.values()){
					leftMaps.put(data.getLeftPath(dif), am.get(data.getLeftPath(dif), TiledMap.class));
					rightMaps.put(data.getRightPath(dif), am.get(data.getRightPath(dif), TiledMap.class));
				}				
			}
			leftMaps.put(Constants.SYNC_LEFT_MAP, am.get(Constants.SYNC_LEFT_MAP, TiledMap.class));
			rightMaps.put(Constants.SYNC_RIGHT_MAP, am.get(Constants.SYNC_RIGHT_MAP, TiledMap.class));
		}
	}
	
	public class AssetFonts{
		
		public final BitmapFont defaultFont;
		AssetFonts(){
			defaultFont = new BitmapFont();
			defaultFont.setUseIntegerPositions(false);
		}
	}
}
