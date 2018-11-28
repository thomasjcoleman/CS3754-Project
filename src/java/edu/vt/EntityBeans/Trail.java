/*
 * Created by Thomas Coleman on 2018.11.15
 * Copyright © 2018 Thomas Coleman. All rights reserved.
 */
package edu.vt.EntityBeans;

import java.util.Date;

public class Trail {
  // Properties
  private Long id;
  private String name;
  private String location;
  private String url;
  private String summary;
  private String difficulty;
  private Double rating;
  private Long numberOfRatings;
  private String imgUrl;
  
  private Double longitude;
  private Double latitude;
  
  private Double length;
  private Double ascentDist;
  private Double descentDist;
  private Double highestHeight;
  private Double lowestHeight;
  
  private String conditionStatus;
  private String conditionDetails;
  private Date conditionDate;
  
  // Constructor
  public Trail() {
  }
  
  public Trail(Long id, String name, String location, String url, String summary, String difficulty, Double rating, Long numberOfRatings, String imgUrl, Double longitude, Double latitude, Double length, Double ascentDist, Double descentDist, Double highestHeight, Double lowestHeight, String conditionStatus, String conditionDetails, Date conditionDate) {
    this.id = id;
    this.name = name;
    this.location = location;
    this.url = url;
    this.summary = summary;
    this.difficulty = difficulty;
    this.rating = rating;
    this.numberOfRatings = numberOfRatings;
    this.imgUrl = imgUrl;
    this.longitude = longitude;
    this.latitude = latitude;
    this.length = length;
    this.ascentDist = ascentDist;
    this.descentDist = descentDist;
    this.highestHeight = highestHeight;
    this.lowestHeight = lowestHeight;
    this.conditionStatus = conditionStatus;
    this.conditionDetails = conditionDetails;
    this.conditionDate = conditionDate;
  }
  
  // Getters/setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public String getDifficulty() {
    return difficulty;
  }

  public void setDifficulty(String difficulty) {
    this.difficulty = difficulty;
  }

  public Double getRating() {
    return rating;
  }

  public void setRating(Double rating) {
    this.rating = rating;
  }

  public Long getNumberOfRatings() {
    return numberOfRatings;
  }

  public void setNumberOfRatings(Long numberOfRatings) {
    this.numberOfRatings = numberOfRatings;
  }

  public String getImgUrl() {
    return imgUrl;
  }

  public void setImgUrl(String imgUrl) {
    this.imgUrl = imgUrl;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public Double getLength() {
    return length;
  }

  public void setLength(Double length) {
    this.length = length;
  }

  public Double getAscentDist() {
    return ascentDist;
  }

  public void setAscentDist(Double ascentDist) {
    this.ascentDist = ascentDist;
  }

  public Double getDescentDist() {
    return descentDist;
  }

  public void setDescentDist(Double descentDist) {
    this.descentDist = descentDist;
  }

  public Double getHighestHeight() {
    return highestHeight;
  }

  public void setHighestHeight(Double highestHeight) {
    this.highestHeight = highestHeight;
  }

  public Double getLowestHeight() {
    return lowestHeight;
  }

  public void setLowestHeight(Double lowestHeight) {
    this.lowestHeight = lowestHeight;
  }

  public String getConditionStatus() {
    return conditionStatus;
  }

  public void setConditionStatus(String conditionStatus) {
    this.conditionStatus = conditionStatus;
  }

  public String getConditionDetails() {
    return conditionDetails;
  }

  public void setConditionDetails(String conditionDetails) {
    this.conditionDetails = conditionDetails;
  }

  public Date getConditionDate() {
    return conditionDate;
  }

  public void setConditionDate(Date conditionDate) {
    this.conditionDate = conditionDate;
  }
}