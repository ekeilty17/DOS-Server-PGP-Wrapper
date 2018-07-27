package com.dnastack.pgp.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dnastack.pgp.model.Ga4ghDataBundle;
import com.dnastack.pgp.model.Ga4ghDataObject;
import com.google.gson.Gson;

public class PGPHttp {
	
	private String url = "https://personalgenomes.ca/v1/public/files/";
	
	// Only reason this is necessary is because the PGP data has this strange
	// "additionalFiles" field that contains other data objects
	public JSONArray flattenData(JSONArray content) {
		
		JSONArray pgp_flattened = new JSONArray();
		
    	for (int i = 0; i < content.length(); i++) {
    		
    		if (content.getJSONObject(i).has("additionalFiles")) {
    			JSONArray internal = flattenData(content.getJSONObject(i).getJSONArray("additionalFiles"));
    			for (int j = 0; j < internal.length(); j++) {
    				pgp_flattened.put(internal.getJSONObject(j));
    			}
    		}
    		
    		content.getJSONObject(i).remove("additionalFiles");
    		pgp_flattened.put(content.getJSONObject(i));
    	}
    	return pgp_flattened;
	}
	
	public JSONArray getData() throws IOException {
		
		// Getting Pagination information
    	String pgp_allFilesResults = new BufferedReader(new InputStreamReader(new URL(this.url).openStream())).readLine();
    	int totalPages = new JSONObject(pgp_allFilesResults).getInt("totalPages");
    	
    	// Iterating through pages to load all data
    	JSONArray pgp_allData = new JSONArray();
    	for (int i = 0; i < totalPages; i++) {
    		// Getting JSON Array of objects
    		pgp_allFilesResults = new BufferedReader(new InputStreamReader(new URL(this.url + "?page=" + String.valueOf(i)).openStream())).readLine();
        	JSONObject pgp_allFilesJSON = new JSONObject(pgp_allFilesResults);
        	JSONArray pgp_allFilesContents = pgp_allFilesJSON.getJSONArray("content");
        	
        	// Flattening them because of the strange "additionalFiles" field
        	JSONArray pgp_flattened = flattenData(pgp_allFilesContents);
        	for (int j = 0; j < pgp_flattened.length(); j++) {
        		pgp_allData.put(pgp_flattened.getJSONObject(j));
        	}
    	}
    	
    	/*
    	for (int i = 0; i < pgp_allData.length(); i++) {
    		System.out.println(pgp_allData.getJSONObject(i).toString());
    	}
    	System.out.println('\n');
    	*/
    	return pgp_allData;
	}
	
	public void postDataObjects(JSONArray allData, String postUrl) throws ClientProtocolException, IOException {
    	
    	for (int i = 0; i < allData.length(); i++) {
    		HttpClient httpClient = HttpClientBuilder.create().build();
    		HttpPost post = new HttpPost(postUrl);
    		
    		Gson gson = new Gson();
    		String json = gson.toJson(new Ga4ghDataObject(allData.getJSONObject(i)));
    		System.out.println("Added: " + json);
    		json = "{\"data_object\":" + json + "}";
    		
    		post.setEntity(new StringEntity(json));
    		post.setHeader("Content-type", "application/json");
    		httpClient.execute(post);
    	}
    	System.out.println('\n');
    }
	
	public void postDataBundles(JSONArray allData, String postUrl) throws ClientProtocolException, IOException {
		
		// Finding max id of participants
		int max_participant = 0;
		for (int i = 0; i < allData.length(); i++) {
			if (allData.getJSONObject(i).getJSONObject("participant").getInt("assignedIdentityNumber") > max_participant) {
				max_participant = allData.getJSONObject(i).getJSONObject("participant").getInt("assignedIdentityNumber");
			}
		}
	
		// Iterating
		for (int i = 0; i < max_participant+1; i++) {
			// Getting data we need from the data set
			List<String> data_object_ids = new ArrayList<String>();
			String created = null;
			String updated = null;
			for (int j = 0; j < allData.length(); j++) {
				JSONObject participant_json = allData.getJSONObject(j).getJSONObject("participant");
				if (participant_json.getInt("assignedIdentityNumber") == i) {
					data_object_ids.add(String.valueOf(allData.getJSONObject(j).getInt("id")));
					created = participant_json.getString("createdAt");
					updated = participant_json.getString("lastModificationAt");
				}
			}
			
			System.out.println(String.valueOf(i) + ' ' + data_object_ids.toString());
			
			// Posting to server
			if (!data_object_ids.isEmpty()) {
				Gson gson = new Gson();
				String json = gson.toJson(new Ga4ghDataBundle(String.valueOf(i), data_object_ids, created, updated));
				json = "{\"data_bundle\":" + json + "}";
				//System.out.println(json);
				
				HttpClient httpClient = HttpClientBuilder.create().build();
				HttpPost post = new HttpPost(postUrl);
				post.setEntity(new StringEntity(json));
				post.setHeader("Content-type", "application/json");
				httpClient.execute(post);
			}
		}
		
	}
}
