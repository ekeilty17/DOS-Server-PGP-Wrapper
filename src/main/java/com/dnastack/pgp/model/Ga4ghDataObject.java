package com.dnastack.pgp.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

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
	
	public Ga4ghDataObject(JSONObject pgp_data) {
		
		this.id = String.valueOf(pgp_data.getInt("id"));
		this.name = pgp_data.getString("filename");
		this.size = String.valueOf(pgp_data.getLong("fileSize"));
		this.mimeType = pgp_data.getString("fileType");
		this.created = pgp_data.getString("createdAt");
		
		if (pgp_data.has("lastModificationAt")) {
			this.updated = pgp_data.getString("lastModificationAt");
		} else {
			this.updated = pgp_data.getString("createdAt");
		}
		
		Map<String, String> system_metadata = new HashMap<String, String>();
		Map<String, String> user_metadata = new HashMap<String, String>();
		user_metadata.put("participant", pgp_data.getJSONObject("participant").toString());
		this.urls = new ArrayList<URL>(Arrays.asList(new URL("https://personalgenomes.ca/v1/public/files/" + String.valueOf(pgp_data.getInt("id")) + "/download", system_metadata, user_metadata)));
		
		this.version = "1.0.0";
		this.checksums = new ArrayList<Checksum>();
		this.description = null;
		this.aliases = new ArrayList<String>();
	}
	
}
