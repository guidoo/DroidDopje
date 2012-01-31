package nl.vanstormbroek.DroidDopje;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="bierdopje")
public class BierdopjeShowList {
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
	public List<BierdopjeShow> shows;
	
	@Override
	public String toString() {
		return "Shows in list: " + shows.size();
	}
	
	public int size() {
		return shows.size();
	}
	
}
