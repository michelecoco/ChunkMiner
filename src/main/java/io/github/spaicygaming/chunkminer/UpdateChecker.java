package io.github.spaicygaming.chunkminer;

import io.github.spaicygaming.chunkminer.util.ChatUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Check for plugin updates using spiget.org API
 */
public class UpdateChecker {

    // Get versions in descending order
    @SuppressWarnings("FieldCanBeLocal")
    private final String VERSION_URL = "https://api.spiget.org/v2/resources/54969/versions?sort=-name";

    private final double currentVersion;
    private double latestVersion;

    public UpdateChecker(double currentVersion) {
        this.currentVersion = currentVersion;

        try {
            URL url = new URL(VERSION_URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            InputStream inputStream = urlConnection.getInputStream();
            InputStreamReader isReader = new InputStreamReader(inputStream);

            // Using Json API instead of Gson because spigot pre-1.8 spigot versions don't contain com.google.gson library
            JSONArray jsonArray = (JSONArray) JSONValue.parseWithException(isReader);
            JSONObject latestVersionObj = (JSONObject) jsonArray.get(0);

            latestVersion = Double.parseDouble(latestVersionObj.get("name").toString());

        } catch (IOException | ParseException ex) {
            ChatUtil.alert("Can't check for updates!");
        }
    }

    /**
     * Checks whether there is are available updates
     *
     * @return true if there is at least one
     */
    public boolean availableUpdate() {
        return latestVersion > currentVersion;
    }

    /**
     * @return the running plugin's version
     */
    public double getCurrentVersion() {
        return currentVersion;
    }

    /**
     * @return the last plugin release version
     */
    public double getLatestVersion() {
        return latestVersion;
    }

}
