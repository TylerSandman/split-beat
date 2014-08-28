package com.splitbeat.game;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.badlogic.gdx.utils.XmlWriter;
import com.splitbeat.game.Constants.NoteSlot;

public class TmxMapBuilder {
	
	private XmlReader mXmlReader;
	private Element mRoot;
	private SongData mData;
	private SongData mPendingChangeData;
	boolean mLeft;
	private String mSongPath;
	private Difficulty mDifficulty;
	
	TmxMapBuilder(boolean left){	
		mLeft = left;
	}
	
	public void create(SongData data, Difficulty difficulty){
		
		mData = data;
		mPendingChangeData = data;
		mDifficulty = difficulty;
		String difStr = "";
		switch (difficulty){
		case Easy:
			difStr = "easy";
			break;
		case Medium:
			difStr = "medium";
			break;
		case Hard:
			difStr = "hard";
			break;
		}
		
		mSongPath = 
				Constants.DEFAULT_MAPS_PATH + "/" + mData.getName() + "/" + 
				mData.getName().toLowerCase().replace(" ", "_");
		mSongPath += "_" + difStr + (mLeft ? "_left" : "_right") + ".tmx";
		mXmlReader = new XmlReader();		
		init();
	}
	
	private void init(){	
		
		//Number of beats in the song multiplied by how many notes are in a measure
		int mapWidth = (int) Math.ceil(mData.getBpm() * mData.getLength() / 60.f * Constants.MEASURE_WIDTH_NOTES);		
	
		//Root element containing Tiled Map data
		mRoot = new Element("map", null);
		
		//Map attributes
		mRoot.setAttribute("version", "1.0");
		mRoot.setAttribute("orientation", "orthogonal");
		//Calculate the number of beats
		mRoot.setAttribute("width", Integer.toString(mapWidth));
		mRoot.setAttribute("height", "3");
		mRoot.setAttribute("tilewidth", "1");
		mRoot.setAttribute("tileheight", "1");
		
		//Properties like bpm, offset, artist, etc
		Element propsEle = new Element("properties", mRoot);
		mRoot.addChild(propsEle);
		
		Element bpmEle = new Element("property", propsEle);
		bpmEle.setAttribute("name", "bpm");
		bpmEle.setAttribute("value", Float.toString(mData.getBpm()));
		propsEle.addChild(bpmEle);
		
		Element offsetEle = new Element("property", propsEle);
		offsetEle.setAttribute("name", "offset");
		offsetEle.setAttribute("value", Float.toString(mData.getOffset()));
		propsEle.addChild(offsetEle);
		
		Element titleEle = new Element("property", propsEle);
		titleEle.setAttribute("name", "title");
		titleEle.setAttribute("value", mData.getTitle());
		propsEle.addChild(titleEle);
		
		Element artistEle = new Element("property", propsEle);
		artistEle.setAttribute("name", "artist");
		artistEle.setAttribute("value", mData.getArtist());
		propsEle.addChild(artistEle);
		
		Element lengthEle = new Element("length", propsEle);
		lengthEle.setAttribute("name", "length");
		lengthEle.setAttribute("value", Integer.toString(mData.getLength()));
		propsEle.addChild(lengthEle);
			
		//Notes
		Element notes = new Element("objectgroup", mRoot);
		mRoot.addChild(notes);
		notes.setAttribute("name", "Notes");
		notes.setAttribute("width", Integer.toString(mapWidth));
		notes.setAttribute("height", "3");
		
		//Holds
		Element holds = new Element("objectgroup", mRoot);
		mRoot.addChild(holds);
		holds.setAttribute("name", "Holds");
		holds.setAttribute("width", Integer.toString(mapWidth));
		holds.setAttribute("height", "3");
		
		//Markers
		Element markers = new Element("objectgroup", mRoot);
		mRoot.addChild(markers);
		markers.setAttribute("name", "Markers");
		markers.setAttribute("width", Integer.toString(mapWidth));
		markers.setAttribute("height", "3");	
	}
	
	public void save(){
		try {
			
			Writer writer = Gdx.files.local(mSongPath).writer(false);
			mSongPath = mSongPath
					.replace(
						mData.getName().toLowerCase().replace(" ", "_"),
						mPendingChangeData.getName().toLowerCase().replace(" ", "_"))
					.replace(
						mData.getName(), mPendingChangeData.getName());
			mData = new SongData(mPendingChangeData);
			XmlWriter xmlWriter = new XmlWriter(writer);
			xmlWriter
			.element("map")
				.attribute("version", mRoot.getAttribute("version"))
				.attribute("orientation", mRoot.getAttribute("orientation"))
				.attribute("width", mRoot.getAttribute("width"))
				.attribute("height", mRoot.getAttribute("height"))
				.attribute("tilewidth", mRoot.getAttribute("tilewidth"))
				.attribute("tileheight", mRoot.getAttribute("tileheight"))					
				.element("properties")
					.element("property")
						.attribute("name", "bpm")
						.attribute("value", mRoot.getChildByName("properties").getChild(0).getAttribute("value"))
					.pop()
					.element("property")
						.attribute("name", "offset")
						.attribute("value", mRoot.getChildByName("properties").getChild(1).getAttribute("value"))
					.pop()
					.element("property")
						.attribute("name", "title")
						.attribute("value", mRoot.getChildByName("properties").getChild(2).getAttribute("value"))
					.pop()
					.element("property")
						.attribute("name", "artist")
						.attribute("value", mRoot.getChildByName("properties").getChild(3).getAttribute("value"))
					.pop()
					.element("property")
						.attribute("name", "length")
						.attribute("value", mRoot.getChildByName("properties").getChild(4).getAttribute("value"))
					.pop()					
				.pop();
				//Notes
				xmlWriter
				.element("objectgroup")
					.attribute("name", "Notes")
					.attribute("width", mRoot.getAttribute("width"))
					.attribute("height", mRoot.getAttribute("height"));
				Element noteGroup = mRoot.getChild(1);
				for (int i = 0; i < noteGroup.getChildCount(); ++i){
					xmlWriter
						.element("object")
							.element("properties")
								.element("property")
									.attribute("name", "type")
									.attribute("value", noteGroup.getChild(i).getChild(0).getChild(0).get("value"))
								.pop()
								.element("property")
									.attribute("name", "beat")
									.attribute("value", noteGroup.getChild(i).getChild(0).getChild(1).get("value"))
								.pop()
								.element("property")
									.attribute("name", "slot")
									.attribute("value", noteGroup.getChild(i).getChild(0).getChild(2).get("value"))
								.pop()
							.pop()
						.pop();
				}
				xmlWriter.pop()
				//Holds
				.element("objectgroup")
					.attribute("name", "Holds")
					.attribute("width", mRoot.getAttribute("width"))
					.attribute("height", mRoot.getAttribute("height"));
				Element holdGroup = mRoot.getChild(2);
				for (int i = 0; i < holdGroup.getChildCount(); ++i){
					xmlWriter
						.element("object")
							.element("properties")
								.element("property")
									.attribute("name", "type")
									.attribute("value", holdGroup.getChild(i).getChild(0).getChild(0).get("value"))
								.pop()
								.element("property")
									.attribute("name", "beat")
									.attribute("value", holdGroup.getChild(i).getChild(0).getChild(1).get("value"))
								.pop()
								.element("property")
									.attribute("name", "slot")
									.attribute("value", holdGroup.getChild(i).getChild(0).getChild(2).get("value"))
								.pop()
								.element("property")
									.attribute("name", "hold")
									.attribute("value", holdGroup.getChild(i).getChild(0).getChild(3).get("value"))
								.pop()
							.pop()
						.pop();
				}
				xmlWriter.pop()
				//Markers
				.element("objectgroup")
					.attribute("name", "Markers")
					.attribute("width", mRoot.getAttribute("width"))
					.attribute("height", mRoot.getAttribute("height"))
				.pop()
			.pop();
			xmlWriter.close();
			Assets.instance.maps.updateMap(mData.getName(), mDifficulty, mData);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void updateSongData(SongData data){
		
		mPendingChangeData = new SongData(data);	
		
		int mapWidth = (int) Math.ceil(mPendingChangeData.getBpm() * mPendingChangeData.getLength() / 60.f * Constants.MEASURE_WIDTH_NOTES);	
		mRoot.setAttribute("width", Integer.toString(mapWidth));
		Element propertiesEle = mRoot.getChild(0);
		
		Element bpmEle = propertiesEle.getChild(0);
		bpmEle.setAttribute("name", "bpm");
		bpmEle.setAttribute("value", Float.toString(mPendingChangeData.getBpm()));
		
		Element offsetEle = propertiesEle.getChild(1);
		offsetEle.setAttribute("name", "offset");
		offsetEle.setAttribute("value", Float.toString(mPendingChangeData.getOffset()));
		
		Element titleEle = propertiesEle.getChild(2);
		titleEle.setAttribute("name", "title");
		titleEle.setAttribute("value", mPendingChangeData.getTitle());
		
		Element artistEle = propertiesEle.getChild(3);
		artistEle.setAttribute("name", "artist");
		artistEle.setAttribute("value", mPendingChangeData.getArtist());		
	}
	
	public void addNote(Note note){
		
		Element noteGroup = mRoot.getChild(1);
		Element noteEle = new Element("object", noteGroup);
		Element propEle = new Element("properties", noteEle);
		
		Element typeEle = new Element("property", propEle);
		typeEle.setAttribute("name", "type");
		typeEle.setAttribute("value", note.type.toString());
		propEle.addChild(typeEle);
		
		Element beatEle = new Element("property", propEle);
		beatEle.setAttribute("name", "beat");
		beatEle.setAttribute("value", Float.toString(note.beat));
		propEle.addChild(beatEle);
		
		Element slotEle = new Element("property", propEle);
		slotEle.setAttribute("name", "slot");
		slotEle.setAttribute("value", note.slot.toString());
		propEle.addChild(slotEle);
		
		noteEle.addChild(propEle);
		noteGroup.addChild(noteEle);
	}
	
	public void removeNote(Note note){
		
		Element noteGroup = mRoot.getChild(1);
		for (int i = 0; i < noteGroup.getChildCount(); ++i){
			
			Element noteEle = noteGroup.getChild(i);
			Element beatEle = noteEle.getChild(0).getChild(1);
			Element slotEle = noteEle.getChild(0).getChild(2);
			float beat = Float.parseFloat(beatEle.getAttribute("value"));
			NoteSlot slot = NoteSlot.stringToSlot(slotEle.getAttribute("value"));
			if (Math.abs(beat - note.beat) < 1.f / 64 && note.slot == slot){
				noteEle.remove();
				return;
			}
		}
	}
	
	public void addNotes(ArrayList<Note> notes){
		
		for(Note note : notes)
			addNote(note);
	}
	
	public void removeNotes(ArrayList<Note> notes){
		
		for(Note note : notes)
			removeNote(note);
	}
	
	public void addHold(HoldNote hold){
		
		Element holdGroup = mRoot.getChild(2);
		Element holdEle = new Element("object", holdGroup);
		Element propEle = new Element("properties", holdEle);
		
		Element typeEle = new Element("property", propEle);
		typeEle.setAttribute("name", "type");
		typeEle.setAttribute("value", hold.type.toString());
		propEle.addChild(typeEle);
		
		Element beatEle = new Element("property", propEle);
		beatEle.setAttribute("name", "beat");
		beatEle.setAttribute("value", Float.toString(hold.beat));
		propEle.addChild(beatEle);
		
		Element slotEle = new Element("property", propEle);
		slotEle.setAttribute("name", "slot");
		slotEle.setAttribute("value", hold.slot.toString());
		propEle.addChild(slotEle);
		
		Element holdDurEle = new Element("property", propEle);
		holdDurEle.setAttribute("name", "hold");
		holdDurEle.setAttribute("value", Float.toString(hold.getHoldDuration()));
		propEle.addChild(holdDurEle);
		
		holdEle.addChild(propEle);
		holdGroup.addChild(holdEle);
	}
	
	public void removeHold(HoldNote hold){
		
		Element noteGroup = mRoot.getChild(2);
		for (int i = 0; i < noteGroup.getChildCount(); ++i){
			
			Element noteEle = noteGroup.getChild(i);
			Element beatEle = noteEle.getChild(0).getChild(1);
			Element slotEle = noteEle.getChild(0).getChild(2);
			float beat = Float.parseFloat(beatEle.getAttribute("value"));
			NoteSlot slot = NoteSlot.stringToSlot(slotEle.getAttribute("value"));
			if (Math.abs(beat - hold.beat) < 1.f / 64 && hold.slot == slot){
				noteEle.remove();
				return;
			}
		}
	}
	
	public void addHolds(ArrayList<HoldNote> holds){
		
		for(HoldNote hold : holds){
			addHold(hold);
		}
	}
	
	public void removeHolds(ArrayList<HoldNote> holds){
		
		for(HoldNote hold : holds)
			removeHold(hold);
	}
	
	public void addMarker(BPMMarker marker){
		
	}
	
	public void addMarkers(ArrayList<BPMMarker> markers){
		
	}

}
