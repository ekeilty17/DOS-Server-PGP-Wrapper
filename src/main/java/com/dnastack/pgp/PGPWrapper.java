package com.dnastack.pgp;

import com.dnastack.pgp.http.PGPHttp;

import java.io.IOException;

import org.json.JSONArray;

public class PGPWrapper 
{
	public static void main( String[] args ) throws IOException
    {
    	PGPHttp pgpHttp = new PGPHttp();
		
		JSONArray allData = pgpHttp.getData();
		pgpHttp.postDataObjects(allData, "http://localhost:8080/dataobjects");
		System.out.println("Number of elements added: " + allData.length() + "\n\n");
		
		pgpHttp.postDataBundles(allData, "http://localhost:8080/databundles");
    	System.out.println("Data Bundles added" + "\n\n");
		
    }
}
