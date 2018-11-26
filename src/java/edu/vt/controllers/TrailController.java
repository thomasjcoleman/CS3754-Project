/*
 * Created by Thomas Coleman on 2018.11.15
 * Copyright © 2018 Thomas Coleman. All rights reserved.
 */
package edu.vt.controllers;

import edu.vt.EntityBeans.Trail;
import edu.vt.globals.Methods;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;

@SessionScoped
@Named(value = "trailController")
public class TrailController implements Serializable {

  // API information
  private final String apiKey = "&key=200392046-9c791c4bdcbda3ac77e4fea595465cf7";
  private final String apiUrl = "https://www.hikingproject.com/data/";

  // Properties
  private List<Trail> trailList;
  private Trail selected;

  // Constructor
  public TrailController() {
  }

  // Get a trail's information via its ID.
  // TODO: testing
  public Trail getTrailByID(int ID) {
    try {
      String jsonStr = readUrlContent(apiUrl + "get-trails-by-id?ids=" + ID + apiKey);
      JSONObject jsonData = (JSONObject) new JSONObject(jsonStr);
      if (jsonData.optInt("success", 0) == 0) {
        Methods.showMessage("Fatal Error", "No trail found with ID " + ID + ".", "");
      } else {
        JSONObject trail = (JSONObject) jsonData.getJSONArray("trails").get(0);
        return createTrailFromJSON(trail);
      }
    } catch (Exception ex) {
      Methods.showMessage("Fatal Error",
              "Hiking project API is unreachable!",
              "See: " + ex.getMessage());
    }
    return null;
  }
  
  // Get trails within an area
  // TODO: testing
  public List<Trail> getTrailsInRadius(double lat, double lon, double maxDistance) {
    String searchData = String.format("?lat=%f&lon=&f&maxDistance=&f", lat, lon, maxDistance);
    try {
      String jsonStr = readUrlContent(apiUrl + "getTrails" + searchData + apiKey);
      JSONObject jsonData = (JSONObject) new JSONObject(jsonStr);
      if (jsonData.optInt("success", 0) == 0) {
        Methods.showMessage("Fatal Error", "No trails found within range.", "");
      } else {
        List<Trail> trails = new ArrayList<Trail>();
        JSONArray trailArray = jsonData.getJSONArray("trails");
        trailArray.forEach(trailObj -> {
          trails.add(createTrailFromJSON((JSONObject) trailObj));
        });
        return trails;
      }
    } catch (Exception ex) {
      Methods.showMessage("Fatal Error",
              "Hiking project API is unreachable!",
              "See: " + ex.getMessage());
    }
    return null;
  }

  /**
   * Create a trail object from a given JSON object.
   *
   * @param trailJson The JSON to parse.
   * @return The created trail.
   */
  private Trail createTrailFromJSON(JSONObject trailJson) {
    Long id = trailJson.optLong("id", 0);
    if (id == 0) {
      return null;
    }

    Double longitude = trailJson.optDouble("longitude");
    if (longitude == Double.NaN) {
      return null;
    }
    Double latitude = trailJson.optDouble("latitude");
    if (latitude == Double.NaN) {
      return null;
    }
    
    String name = trailJson.optString("name", "");
    if (name.equals("")) {
      name = "Name unavailable.";
    }
    String location = trailJson.optString("location", "");
    if (location.equals("")) {
      location = "Location name unavailable.";
    }
    String url = trailJson.optString("url", "");
    String summary = trailJson.optString("summary", "");
    if (summary.equals("")) {
      summary = "No summary available.";
    }
    String difficulty = trailJson.optString("difficulty", "");
    if (difficulty.equals("")) {
      difficulty = "Difficulty unavailable.";
    }
    Double rating = trailJson.optDouble("stars", 0);
    String imgUrl = trailJson.optString("imgMedium", ""); // TODO: default image?

    Double length = trailJson.optDouble("length");
    Double ascentDist = trailJson.optDouble("ascent");
    Double descentDist = trailJson.optDouble("descent");
    Double highestHeight = trailJson.optDouble("high");
    Double lowestHeight = trailJson.optDouble("low");
    
    String conditionStatus = trailJson.optString("conditionStatus", "");
    if (conditionStatus.equals("")) {
      conditionStatus = "Condition status unavailable.";
    }
    String conditionDetails = trailJson.optString("conditionDetails", "");
    if (conditionDetails.equals("")) {
      conditionDetails = "Condition details unavailable.";
    }
    Date conditionDate;
    String conditionDateStr = trailJson.optString("conditionDate", "");
    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      conditionDate = dateFormat.parse(conditionDateStr);
    } catch (ParseException ex) {
      conditionDate = null;
    }
    
    return new Trail(
            id, name, location, url, summary, difficulty,
            rating, imgUrl, longitude, latitude, length,
            ascentDist, descentDist, highestHeight, lowestHeight,
            conditionStatus, conditionDetails, conditionDate
    );
  }

  /**
   * Return the content of a given URL as String
   *
   * @param webServiceURL to retrieve the JSON data from
   * @return JSON data from the given URL as String
   * @throws Exception
   */
  public String readUrlContent(String webServiceURL) throws Exception {
    BufferedReader reader = null;
    try {
      URL url = new URL(webServiceURL);
      reader = new BufferedReader(new InputStreamReader(url.openStream()));
      StringBuilder buffer = new StringBuilder();
      char[] chars = new char[10240];

      int numberOfCharactersRead;
      while ((numberOfCharactersRead = reader.read(chars)) != -1) {
        buffer.append(chars, 0, numberOfCharactersRead);
      }
      return buffer.toString();

    } finally {
      if (reader != null) {
        reader.close();
      }
    }
  }
}
