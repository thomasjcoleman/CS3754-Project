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
import java.nio.charset.Charset;
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

  private final String weatherAPIKey = "af5a8e96d48b2f143623831e3777c43a";
  private final String weatherAPIUrl = "https://api.openweathermap.org/data/2.5/weather";

  // Properties
  private String jsonResults;

  private List<Trail> results;
  private Trail selected;
  private String travelMode = "DRIVING";

  private String locationQuery;
  private String latitudeQuery;
  private String longitudeQuery;
  private String distanceQuery;
  private String minLengthQuery;
  private String searchDifficulty;
  private String searchRating;

  // Constructor
  public TrailController() {
  }

  // Getters/setters
  public String getJsonResults() {
    return jsonResults;
  }

  public void setJsonResults(String jsonResults) {
    this.jsonResults = jsonResults;
  }

  public List<Trail> getResults() {
    return results;
  }

  public void setResults(List<Trail> results) {
    this.results = results;
  }

  public Trail getSelected() {
    return selected;
  }

  public String getLocationQuery() {
    return locationQuery;
  }

  public void setLocationQuery(String locationQuery) {
    this.locationQuery = locationQuery;
  }

  public String getLatitudeQuery() {
    return latitudeQuery;
  }

  public void setLatitudeQuery(String latitudeQuery) {
    this.latitudeQuery = latitudeQuery;
  }

  public String getLongitudeQuery() {
    return longitudeQuery;
  }

  public void setLongitudeQuery(String longitudeQuery) {
    this.longitudeQuery = longitudeQuery;
  }

  public String getDistanceQuery() {
    return distanceQuery;
  }

  public void setDistanceQuery(String distanceQuery) {
    this.distanceQuery = distanceQuery;
  }

  public String getMinLengthQuery() {
    return minLengthQuery;
  }

  public void setMinLengthQuery(String minLengthQuery) {
    this.minLengthQuery = minLengthQuery;
  }

  public String getSearchDifficulty() {
    return searchDifficulty;
  }

  public void setSearchDifficulty(String searchDifficulty) {
    this.searchDifficulty = searchDifficulty;
  }

  public String getSearchRating() {
    return searchRating;
  }

  public void setSearchRating(String searchRating) {
    this.searchRating = searchRating;
  }

  public void setSelected(Trail selected) {
    this.selected = selected;
  }

  public String getTravelMode() {
    return travelMode;
  }

  public void setTravelMode(String travelMode) {
    this.travelMode = travelMode;
  }

  public String changeTravelMode(int type) {
    switch (type) {
      case 0:
        travelMode = "WALKING";
        break;
      case 1:
        travelMode = "DRIVING";
        break;
      case 2:
        travelMode = "BICYCLING";
        break;
      case 3:
        travelMode = "TRANSIT";
        break;
    }

    return "TrailDirections.xhtml?id=" + selected.getId() + "&faces-redirect=true";
  }

  /* Instance methods */
  // Get all nearby trail data.
  public void getMainMap() {
    getTrailsInRadius(37.227264, -80.420745, 100);
  }

  // Get a specific trail's data.
  public void getTrailMap(Integer ID) {
    if (ID != null) {
      selected = getTrailByID(ID);
    } else if (selected != null) {
      getTrailByID(selected.getId());
    }
  }

  // Get a trail's information via its ID.
  public Trail getTrailByID(Integer ID) {
    try {
      jsonResults = readUrlContent(apiUrl + "get-trails-by-id?ids=" + ID + apiKey);
      JSONObject jsonData = (JSONObject) new JSONObject(jsonResults);
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

  // Get trails within an area.
  public List<Trail> getTrailsInRadius(double lat, double lon, double maxDistance) {
    // Form the search string.
    String searchData = String.format("?lat=%f&lon=%f&maxDistance=%f&maxResults=150", lat, lon, maxDistance);
    try {
      // Fetch the results from the page, and parse it as a JSON object.
      jsonResults = readUrlContent(apiUrl + "get-trails" + searchData + apiKey);
      JSONObject jsonData = (JSONObject) new JSONObject(jsonResults);
      if (jsonData.optInt("success", 0) == 0) {
        // If the "success" value is 0, then the seach failed to find anything.
        Methods.showMessage("Fatal Error", "No trails found within range.", "");
      } else {
        // Otherwise, form a list from all the collected trails.
        List<Trail> trails = new ArrayList<>();
        JSONArray trailArray = jsonData.getJSONArray("trails");
        trailArray.forEach(trailObj -> {
          trails.add(createTrailFromJSON((JSONObject) trailObj));
        });
        return trails;
      }
    } catch (Exception ex) {
      // The API could not be reached or a fatal error occurred.
      Methods.showMessage("Fatal Error",
              "Hiking project API is unreachable!",
              "See: " + ex.getMessage());
    }
    return null;
  }

  // Search Trail given specific paramters
  public String performSearch() {
    selected = null;
    results = new ArrayList();
    // Form the search string
    String searchData = "?lat=" + latitudeQuery + "&lon=" + longitudeQuery + "&maxDistance=" + distanceQuery + "&maxResults=250";
    try {
      jsonResults = readUrlContent(apiUrl + "get-trails" + searchData + apiKey);
      //jsonResults = readUrlContent(apiUrl + "get-trails" + searchData + apiKey);
      JSONObject jsonData = (JSONObject) new JSONObject(jsonResults);
      if (jsonData.optInt("success", 0) == 0) {
        Methods.showMessage("Fatal Error", "No trails found within range.", "");
      } else {
        JSONArray trailArray = jsonData.getJSONArray("trails");
        trailArray.forEach(trailObj -> {
          results.add(createTrailFromJSON((JSONObject) trailObj));
        });
        // Filter any trails that don't match our paramters
        if (!searchDifficulty.equals("0")) {
          results.removeIf(p -> !p.getDifficulty().equals(searchDifficulty));
        }
        if (Double.valueOf(searchRating) != 0) {
          results.removeIf(p -> p.getRating() < Double.valueOf(searchRating));
        }
        results.removeIf(p -> p.getLength() < Double.valueOf(minLengthQuery));
        return "/findTrails/Results?faces-redirect=true";
      }
    } catch (Exception ex) {
      Methods.showMessage("Fatal Error",
              "Hiking project API is unreachable!",
              "See: " + ex.getMessage());
    }
    return "";
  }

  /**
   * Create a trail object from a given JSON object.
   *
   * @param trailJson The JSON to parse.
   * @return The created trail.
   */
  private Trail createTrailFromJSON(JSONObject trailJson) {
    Integer id = trailJson.optInt("id", 0);
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
    Long numberOfRatings = trailJson.optLong("starVotes", 0);
    String imgUrl = trailJson.optString("imgMedium", "");
    if (imgUrl.equals("")) {
      imgUrl = "../resources/images/noImage.jpg";
    }

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
            rating, numberOfRatings, imgUrl, longitude, latitude,
            length, ascentDist, descentDist, highestHeight, lowestHeight,
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
      reader = new BufferedReader(new InputStreamReader(url.openStream(), Charset.forName("UTF-8")));
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

  public String reload() {
    return "TrailDetails.xhtml?faces-redirect=true&id=" + selected.getId();
  }

  public void clear() {
    locationQuery = null;
    latitudeQuery = null;
    longitudeQuery = null;
    distanceQuery = null;
    minLengthQuery = null;
    searchDifficulty = null;
    searchRating = null;
  }

  // Get the weather API call string for a given latitude/longitude.
  private String formWeatherUrl(String lat, String lon) {
    return weatherAPIUrl
            + "?lat=" + lat
            + "&lon=" + lon
            + "&appid=" + weatherAPIKey;

  }

  // Get the temperature at the selected trail's latitude/longitude.
  public String getTemperature() {
    try {
      String json = readUrlContent(formWeatherUrl(selected.getLatitude().toString(), selected.getLongitude().toString()));
      JSONObject jsonData = (JSONObject) new JSONObject(json);
      JSONObject weatherObject = jsonData.getJSONObject("main");
      String retVal = weatherObject.optString("temp", "");
      Double temp = Double.parseDouble(retVal);
      //convert K to F: (K − 273.15) × 9/5 + 32
      temp = round((temp - 273.15) * (9 / 5) + 32, 1);
      retVal = temp.toString();
      return retVal;
    } catch (Exception ex) {
      return "";
    }
  }

  // Fetch the weather at the selected trail's latitude/longitude
  public String getWeatherConditions() {
    try {
      String json = readUrlContent(formWeatherUrl(selected.getLatitude().toString(), selected.getLongitude().toString()));
      JSONObject jsonData = (JSONObject) new JSONObject(json);
      JSONArray weatherArray = jsonData.getJSONArray("weather");
      JSONObject obj = weatherArray.getJSONObject(0);
      String input = obj.optString("description", "");
      return input.substring(0, 1).toUpperCase() + input.substring(1);
    } catch(Exception ex) {
      return "";
    }
  }

  private static double round(double value, int precision) {
    int scale = (int) Math.pow(10, precision);
    return (double) Math.round(value * scale) / scale;
  }

}
