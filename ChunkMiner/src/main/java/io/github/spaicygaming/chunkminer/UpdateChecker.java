package io.github.spaicygaming.chunkminer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import io.github.spaicygaming.chunkminer.util.ChatUtil;

public class UpdateChecker {

	// Versions in descending order
	private final String VERSION_URL = "https://api.spiget.org/v2/resources/54969/versions?sort=-name";
	
	private double currentVersion;
	private double latestVersion;
	
	public UpdateChecker(double currentVersion) {
		this.currentVersion = currentVersion;
		
		try {
			URL url = new URL(VERSION_URL);
			HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
			
			InputStream inputStream = urlConnection.getInputStream();
			InputStreamReader isReader = new InputStreamReader(inputStream);
			
			// Using Json API insted of Gson because spigot 1.7.10 and olders don't have com.google.gson library
			JSONArray jsonArray = (JSONArray) JSONValue.parseWithException(isReader);
			JSONObject latestVersionObj = (JSONObject) jsonArray.get(0);
			
			latestVersion = Double.parseDouble(latestVersionObj.get("name").toString());

		} catch (IOException | ParseException ex) {
			ChatUtil.alert("Can't check for updates!");
		}
	}
	
	public boolean availableUpdate() {
		return latestVersion > currentVersion;
	}
	
	public double getCurrentVersion() {
		return currentVersion;
	}
	
	public double getLatestVersion() {
		return latestVersion;
	}
	
}
