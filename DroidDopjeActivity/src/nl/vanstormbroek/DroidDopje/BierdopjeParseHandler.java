package nl.vanstormbroek.DroidDopje;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.ContentValues;
import android.util.Log;

public class BierdopjeParseHandler extends DefaultHandler {
	ContentValues values = new ContentValues();
	String textBetween = new String();
	private static final String TAG = "BierdopjeParseHandler";
	public static final String[] columns = {"showName",	"showId", "TVDbId", 
	"showLink",
	"firstAired",
	"lastAired",
	"nextEpisode",
	"seasons",
	"episodes",
	"genres",
	"showStatus",
	"network",
	"airtime",
	"runtime",
	"score",
	"favorites",
	"has_translators",
	"updated",
	"summary"};

	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
	}
	
	@Override 
	public void characters(char ch[], int start, int length) {
		textBetween = new String(ch, start, length);
	}
	
	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		for (int i=0; i < columns.length; i++) {
			if (columns[i].equalsIgnoreCase(localName)) {		
				values.put(localName, textBetween);
				
				Log.i(TAG, "endelement: " + localName + "=" + textBetween);		
				Log.i(TAG, "val len: " + values.size());
			}
		}
		
	}
	
	@Override
	public void endDocument() throws SAXException {
		
	}
	
	public ContentValues getCV() {
		return values;
	}
}
