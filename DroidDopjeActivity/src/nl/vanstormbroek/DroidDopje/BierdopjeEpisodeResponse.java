package nl.vanstormbroek.DroidDopje;

import java.net.URL;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;


@Root(name="bierdopje", strict=false)
public class BierdopjeEpisodeResponse {

	private final static String TAG = "DROIDDOPJE";

/*
 
	public boolean statusReq;

	@Element(required=false)
	public boolean cached;

	@Element(required=false)
	public String apiversion;
*/
	@Element(required=false)
	public BierdopjeEpisode response;
	
	@Element(required=false, name="results")
	public String episodeCached;

	public boolean isCached() {
		if (episodeCached == null)
			return false;
		return episodeCached.length() != 0;
	}
	
	@Override
	public String toString() {
		if (episodeCached != null) {
			return "BDResponse:: gecached? " + episodeCached.toString();
		} else {
			return "geen cache";
		}
	}

}
