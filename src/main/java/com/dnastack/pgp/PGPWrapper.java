package com.dnastack.pgp;

import com.dnastack.pgp.model.Ga4ghDataBundle;
import com.dnastack.pgp.model.Ga4ghDataObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;


import com.google.gson.Gson;

public class PGPWrapper 
{
	public static List<List<String>> parseData(JSONArray content) {
		
		List<List<String>> pgp_importantData = new ArrayList<List<String>>();
    	for (int i = 0; i < content.length(); i++) {
    		
    		JSONObject object = content.getJSONObject(i);
    		
    		ArrayList<String> pgp_simplified = new ArrayList<String>();
    		pgp_simplified.add(String.valueOf(object.getInt("id")));
    		pgp_simplified.add(object.getString("filename"));
    		pgp_simplified.add(String.valueOf(object.getLong("fileSize")));
    		pgp_simplified.add(object.getString("fileType"));
    		pgp_simplified.add(object.getJSONObject("participant").toString());
    		pgp_simplified.add(object.getString("createdAt"));
    		
    		if (object.has("lastModificationAt")) {
    			pgp_simplified.add(object.getString("lastModificationAt"));
    		}
    		
    		if (object.has("additionalFiles")) {
    			pgp_importantData.addAll(parseData(object.getJSONArray("additionalFiles")));
    		}
    		pgp_importantData.add(pgp_simplified);
    	}
    	
    	// [ [id, filename, fileSize, fileType, participant, creaetedAt, lastModificationAt], ... ]
		return pgp_importantData;
	}
	
	public static List<List<String>> getData(String url) throws IOException {
		
		// Getting Pagination information
    	String pgp_allFilesResults = new BufferedReader(new InputStreamReader(new URL(url).openStream())).readLine();
    	int totalPages = new JSONObject(pgp_allFilesResults).getInt("totalPages");
    	
    	// Iterating through pages to load all data
    	List<List<String>> pgp_importantData = new ArrayList<List<String>>();
    	for (int i = 0; i < totalPages; i++) {
    		pgp_allFilesResults = new BufferedReader(new InputStreamReader(new URL(url + "?page=" + String.valueOf(i)).openStream())).readLine();
        	JSONObject pgp_allFilesJSON = new JSONObject(pgp_allFilesResults);
        	JSONArray pgp_allFilesContents = pgp_allFilesJSON.getJSONArray("content");
        	pgp_importantData.addAll(parseData(pgp_allFilesContents));
    	}
    	
    	for (List<String> s : pgp_importantData) {
    	    System.out.println(s);
    	}
    	System.out.println('\n');
    	
    	return pgp_importantData;
	}
	
	public static HttpResponse postDataObjects(List<String> data, String url) throws ClientProtocolException, IOException {
    	
    	HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);
		
		Gson gson = new Gson();
		String json = gson.toJson(new Ga4ghDataObject(data));
		json = "{\"data_object\":" + json + "}";
		//System.out.println(json);
		
		post.setEntity(new StringEntity(json));
		post.setHeader("Content-type", "application/json");
		HttpResponse response = httpClient.execute(post);
		
		return response;
    }
	
	public static void postDataBundles(List<List<String>> data, String url) throws ClientProtocolException, IOException {
		
		// Finding max id of participants
		int max_participant = 0;
		for (List<String> L : data) {
			JSONObject participant_json = new JSONObject(L.get(4));
			if (participant_json.getInt("assignedIdentityNumber") > max_participant) {
				max_participant = participant_json.getInt("assignedIdentityNumber");
			}
		}
		
		// Iterating
		for (int i = 0; i < max_participant+1; i++) {
			// Getting data we need from the data set
			List<String> data_object_ids = new ArrayList<String>();
			String created = null;
			String updated = null;
			for (List<String> L : data) {
				JSONObject participant_json = new JSONObject(L.get(4));
				if (participant_json.getInt("assignedIdentityNumber") == i) {
					data_object_ids.add(L.get(0));
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
				HttpPost post = new HttpPost(url);
				post.setEntity(new StringEntity(json));
				post.setHeader("Content-type", "application/json");
				httpClient.execute(post);
			}
		}
	}
	
	public static void main( String[] args ) throws IOException
    {
    	List<List<String>> data = new ArrayList<List<String>>();
    	data.addAll(getData("https://personalgenomes.ca/v1/public/files/"));
    	
		data.forEach(d -> {
			try {
				postDataObjects(d, "http://localhost:8080/dataobjects");
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
    	
    	System.out.println("Number of elements added: " + data.size() + "\n\n");
    	
    	postDataBundles(data, "http://localhost:8080/databundles");
    	System.out.println("Data Bundles added" + "\n\n");
		
    }
}
