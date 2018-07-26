package com.dnastack.pgp;

import com.dnastack.pgp.http.PGPHttp;
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
	public static void main( String[] args ) throws IOException
    {
    	PGPHttp pgpHttp = new PGPHttp();
		
		JSONArray allData = pgpHttp.getData("https://personalgenomes.ca/v1/public/files/");
		pgpHttp.postDataObjects(allData, "http://localhost:8080/dataobjects");
		System.out.println("Number of elements added: " + allData.length() + "\n\n");
		
		pgpHttp.postDataBundles(allData, "http://localhost:8080/databundles");
    	System.out.println("Data Bundles added" + "\n\n");
		
    }
}
