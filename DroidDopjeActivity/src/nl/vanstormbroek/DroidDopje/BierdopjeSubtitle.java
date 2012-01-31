package nl.vanstormbroek.DroidDopje;

import java.net.URL;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="result")
public class BierdopjeSubtitle {
	@Element(required=false)
	public String filename;
	
	@Element(required=false)
	public int filesize;
	
	@Element(required=false)
	public String uploader;
	
	@Element(required=false)
	public URL uploaderprofile;
	
	@Element(required=false)
	public String pubdate;
	
	@Element(required=false)
	public int numreplies;
	
	@Element(required=false)
	public int numdownloads;
	
	@Element(required=false)
	public boolean exclusive;
	
	@Element(required=false)
	public URL downloadlink;
	
	@Override
	public String toString() {
		return "This is the link: " + downloadlink.toString();
	}
}
