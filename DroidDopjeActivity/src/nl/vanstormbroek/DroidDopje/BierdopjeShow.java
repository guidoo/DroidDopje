package nl.vanstormbroek.DroidDopje;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import android.database.Cursor;


@Root(name="bierdopje")
public class BierdopjeShow {
	private final static String TAG = "DROIDDOPJE";
	
	@Element(required=false)
	public String response;
	
	@Element(required=true)
	public String showname;	
	
	@Element(required=true)
	public int showid;

	@Element(required=false)
	public int tvdbid;
	
	@Element(required=false)
	public URL showlink;
	
	@Element(required=false)
	public String firstaired;

	@Element(required=false)
	public String lastaired;

	@Element(required=false)
	public String nextepisode;
	
	@Element(required=false)
	public int seasons;
	
	@Element(required=false)
	public int episodes;
	
	@ElementList(required=false)
	public List<String> genres;
	
	@Element(required=false)
	public String showstatus;
	
	@Element(required=false)
	public String network;
	
	@Element(required=false)
	public String airtime;
	
	@Element(required=false)
	public int runtime;
	
	@Element(required=false)
	public String score;
	
	@Element(required=false)
	public int favorites;
	
	@Element(required=false)
	public boolean has_translators;
	
	@Element(required=false)
	public String updated;
	
	@Element(required=false)
	public String summary;
	
	@Element(required=false)
	public boolean status;
	
	@Element(required=false)
	public boolean cached;
	
	@Element(required=false)
	public String apiversion;
	
	public boolean myshow;

	@Override
	public String toString() {
		return " > Show " + showname + " heeft score " + score;
	}
	
	
	
}