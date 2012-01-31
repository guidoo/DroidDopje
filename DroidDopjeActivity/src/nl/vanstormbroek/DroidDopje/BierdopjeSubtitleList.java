package nl.vanstormbroek.DroidDopje;

import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;


@Root(name="bierdopje")
public class BierdopjeSubtitleList {
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
	
	@ElementList(required=false, name="results")
	public List<BierdopjeSubtitle> subtitles; 
	
	@Override
	public String toString() {
		return "List has " + subtitles.size() + " subtitles";
	}
}
