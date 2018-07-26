package com.dnastack.pgp.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class Ga4ghDataObject {

	private String id;
	private String name;
	private String size;
	private String created;
	private String updated;
	private String version;
	private String mimeType;
	private List<Checksum> checksums;
	private List<URL> urls;
	private String description;
	private List<String> aliases;
	
	public Ga4ghDataObject() {
		
	}
	
	public Ga4ghDataObject(String id, String name, String size, String created, String updated, String version,
			String mimeType, List<Checksum> checksums, List<URL> urls, String description, List<String> aliases) {
		super();
		this.id = id;
		this.name = name;
		this.size = size;
		this.created = created;
		this.updated = updated;
		this.version = version;
		this.mimeType = mimeType;
		this.checksums = checksums;
		this.urls = urls;
		this.description = description;
		this.aliases = aliases;
	}
	
	public Ga4ghDataObject(List<String> pgp_data) {
		
		this.id = pgp_data.get(0);
		this.name = pgp_data.get(1);
		this.size = pgp_data.get(2);
		this.mimeType = pgp_data.get(3);
		this.created = pgp_data.get(5);
		
		if (pgp_data.size() == 7) {
			this.updated = pgp_data.get(6);
		} else {
			this.updated = pgp_data.get(5);
		}
		
		Map<String, String> system_metadata = new HashMap<String, String>();
		Map<String, String> user_metadata = new HashMap<String, String>();
		user_metadata.put("participant", pgp_data.get(4));
		this.urls = new ArrayList<URL>(Arrays.asList(new URL("https://personalgenomes.ca/v1/public/files/" + pgp_data.get(0) + "/download", system_metadata, user_metadata)));
		
		this.version = "1.0.0";
		this.checksums = new ArrayList<Checksum>();
		this.description = null;
		this.aliases = new ArrayList<String>();
	}
	
}
