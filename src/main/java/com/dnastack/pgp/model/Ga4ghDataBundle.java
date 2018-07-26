package com.dnastack.pgp.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class Ga4ghDataBundle {

	private String id;
	private List<String> data_object_ids;
	private String created;
	private String updated;
	private String version;
	private List<Checksum> checksums;
	private String description;
	private List<String> aliases;
	private Map<String, String> system_metadata;
	private Map<String, String> user_metadata;
	
	public Ga4ghDataBundle() {
		
	}
	
	public Ga4ghDataBundle(String id, List<String> data_object_ids, String created, String updated, String version,
			List<Checksum> checksums, String description, List<String> aliases, Map<String, String> system_metadata,
			Map<String, String> user_metadata) {
		super();
		this.id = id;
		this.data_object_ids = data_object_ids;
		this.created = created;
		this.updated = updated;
		this.version = version;
		this.checksums = checksums;
		this.description = description;
		this.aliases = aliases;
		this.system_metadata = system_metadata;
		this.user_metadata = user_metadata;
	}
	
	public Ga4ghDataBundle(String participant_id, List<String> data_object_ids, String created, String updated) {
		super();
		this.id = participant_id;
		this.data_object_ids = data_object_ids;
		
		this.created = created;
		this.updated = updated;
		
		this.version = "1.0.0";
		
		this.checksums = new ArrayList<Checksum>();
		this.description = "all data object id's have their participant with id equal to the id of the data bundle";
		this.aliases = new ArrayList<String>();
		this.system_metadata = new HashMap<String, String>();
		this.user_metadata = new HashMap<String, String>();
	}
	
}
