package com.splitbeat.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.ResolutionFileResolver;
import com.badlogic.gdx.assets.loaders.resolvers.ResolutionFileResolver.Resolution;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;

public class Assets implements Disposable, AssetErrorListener{
	
	public static final Assets instance = new Assets();
	private AssetManager mAssetManager;
	public AssetButton button;
	public AssetMap maps;
	public AssetMusic music;
	public AssetFonts fonts;
	
	private Assets(){}
	
	public void init (AssetManager assetManager){
		
		Resolution smallRes = new Resolution(320, 480, "480x320");
		Resolution largeRes = new Resolution(760, 920, "960x720");
		Resolution[] resolutions = new Resolution[]{ smallRes, largeRes };
		ResolutionFileResolver resolver = new ResolutionFileResolver(new InternalFileHandleResolver(), resolutions);
		
		mAssetManager = assetManager;
		mAssetManager.setErrorListener(this);	
		
		//Load level
		mAssetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
		mAssetManager.load(Constants.LEFT_MAPS[0], TiledMap.class);
		mAssetManager.load(Constants.RIGHT_MAPS[0], TiledMap.class);
		mAssetManager.finishLoading();
		
		//Load button textures;
		mAssetManager.setLoader(TextureAtlas.class, new TextureAtlasLoader(resolver));
		mAssetManager.load(Constants.TEXTURE_ATLAS_BUTTONS, TextureAtlas.class);
		mAssetManager.finishLoading();
		
		//Load music
		mAssetManager.load("music/paper_planes.mp3", Music.class);
		mAssetManager.finishLoading();
		
		//Initialize assets
		TextureAtlas atlas = mAssetManager.get(Constants.TEXTURE_ATLAS_BUTTONS);	
		button = new AssetButton(atlas);
		maps = new AssetMap(mAssetManager);
		music = new AssetMusic(mAssetManager);
		fonts = new AssetFonts();
		
	}

	@Override
	public void error(AssetDescriptor asset, Throwable throwable) {
		Gdx.app.error("Asset", "Couldn't load asset '" + asset.fileName + "'", (Exception) throwable);
	}

	@Override
	public void dispose() {	
		mAssetManager.dispose();
		fonts.defaultFont.dispose();
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
		AssetMusic(AssetManager am){
			paperPlanes = am.get("music/paper_planes.mp3", Music.class);
		}	
	}
	
	public class AssetMap{
		
		public final TiledMap left;
		public final TiledMap right;
		AssetMap(AssetManager am){
			left = am.get(Constants.LEFT_MAPS[0]);
			right = am.get(Constants.RIGHT_MAPS[0]);
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
