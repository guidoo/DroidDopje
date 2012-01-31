package nl.vanstormbroek.DroidDopje;

import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;


@Root(name="bierdopje")
public class BierdopjeEpisodeList {
	private final static String TAG = "DROIDDOPJE";
	
	@Element(required=false)
	public String response;
	
	@Element(required=false)
	public boolean status;
	
	@Element(required=false)
	public boolean cached;
	
	@Element(required=false)
	public String apiversion;
	
	@Element(required=false)
	public String cachelife;
	
	//@Element(required=false)
	//public String results;
		
	@ElementList(name="results")
	public List<BierdopjeEpisode> episodes;
	
	@Override
	public String toString() {
		return "Episodes in list: " + episodes.size();
	}
	
}