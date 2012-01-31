package nl.vanstormbroek.DroidDopje;

import java.net.URL;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;


//@Root(name="bierdopje")
@Root(name="bierdopje", strict=false)
public class BierdopjeEpisode {
	private final static String TAG = "DROIDDOPJE";
	
	@Element(required=false)
	public String results;
	
	@Element(required=false)
	public String response;
	
	@Element(required=false)
	public int episodeid;
			
	@Element(required=false)
	public String showname;	
	
	@Element(required=false)
	public String title;	

	@Element(required=false)
	public int tvdbid;
	
	@Element(required=false)
	public URL showlink;
	
	@Element(required=false)
	public URL episodelink;
	
	@Element(required=false)
	public int season;
	
	@Element(required=false)
	public int episode;
	
	@Element(required=false)
	public int epnumber;
	
	@Element(required=false)
	public boolean wip;
	
	@Element(required=false)
	public String wippercentage;
	
	@Element(required=false)
	public String wipuser;
	
	@Element(required=false)
	public String score;
	
	@Element(required=false)
	public int votes;
	
	@Element(required=false)
	public String formatted;
	
	@Element(required=false)
	public String airdate;

	@Element(required=false)
	public boolean is_special;
	
	@Element(required=false)
	public boolean subsnl;
	
	@Element(required=false)
	public boolean subsen;
	
	@Element(required=false)
	public String updated;
	
	@Element(required=false)
	public String summary; 
	
	/*
	@Element(required=false)
	public boolean status;
	
	@Element(required=false)
	public boolean cached;
	
	@Element(required=false)
	public String apiversion;
	*/
	
	@Override
	public String toString() {
		return "Show " + title + "-" + episode + " heeft " + subsnl + " NL ondertiteling";
	}
	
}