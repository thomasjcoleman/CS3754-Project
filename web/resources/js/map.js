/* 
 * Created by Thomas Coleman on 2018.11.15
 * Copyright © 2018 Thomas Coleman. All rights reserved. * 
 */

/* Constants */
var difficultyRatings = {
  green: "Easy",
  greenBlue: "Moderately Easy",
  blue: "Intermediate",
  blueBlack: "Difficult",
  black: "Very Difficult"
};

/* Global variables */
var google;
var map;
var currentMarker = null;

// Instantiate a DirectionsService object
var directionsService = new google.maps.DirectionsService();

// Instantiate a DirectionsRenderer object
var directionsDisplay = new google.maps.DirectionsRenderer();

// Create and display a map centered on Virginia Tech
function initializeMap() {
  map = new google.maps.Map(document.getElementById('map'), {
    zoom: 10,
    center: {lat: 37.227264, lng: -80.420745},
    mapTypeControl: true,
    mapTypeControlOptions: {
      style: google.maps.MapTypeControlStyle.HORIZONTAL_BAR,
      position: google.maps.ControlPosition.BOTTOM_LEFT
    },
    mapTypeId: google.maps.MapTypeId.HYBRID
  });
  display();
}

// Prepare the map object as necessary
function display() {
  // show user's position
  navigator.geolocation.getCurrentPosition(function (pos) {
    var c = pos.coords;
    var marker = new google.maps.Marker({
      position: new google.maps.LatLng(c.latitude, c.longitude),
      title: "Your location",
      icon: "../resources/images/userLocation.png",
      map: map
    });
  });

  if (document.getElementById("trailGPX") !== null) {
    // If there is a specific trail named, only show that one.
    displaySingleTrail();
  } else {
    // Else, show all trails.
    displayAllTrails();
  }

}


// Displays the geolocation of a single trail.
function displaySingleTrail() {
  // Get the trail's information
  var trail = JSON.parse(document.getElementById("jsonData").value).trails[0];
  
  // Fetch the GPX file for the trail and display it on the map.
  var gpxFile = document.getElementById("trailGPX").value;
  loadGPXFileIntoGoogleMap(map, gpxFile)
}


// Displays the geolocations of all VT buildings in the given category
function displayAllTrails() {
  var trailList = JSON.parse(document.getElementById("jsonData").value).trails;
  var numberOfTrails = trailList.length;
  var infoWindow = new google.maps.InfoWindow();
  for (j = 0; j < numberOfTrails; j++) {
    // Make a new marker for the trail
    var trail = trailList[j];
    var marker = new google.maps.Marker({
      position: new google.maps.LatLng(trail.latitude, trail.longitude),
      map: map,
      title: trail.name,
      trailId: trail.id,
      trailImage: trail.imgMedium || "../resources/images/noImage.jpg",
      trailDifficulty: trail.difficulty,
      trailRating: trail.stars,
      trailLength: trail.length || "Unspecified",
      trailSummary: trail.summary || "No description available.",
      trailCondition: trail.conditionStatus || "Unkonwn",
      trailConditionDet: trail.conditionDetails || ""
    });
    marker.setMap(map);

    google.maps.event.addListener(marker, "click", function () {
      infoWindow.setContent(
        "<a href='./TrailDetails.xhtml?id=" + this.get('trailId') + "' target='_blank'><h1>" + this.get('title') + "</h1></a>" +
        "<div style='overflow: auto'><div class='float50'>" +
          "<a href='./TrailDetails.xhtml?id=" + this.get('trailId') + "' target='_blank'>" + 
          "<img class='preview' src='" + this.get('trailImage') + "'/></a><br/>" +
        "</div><div class='float50'>" +
          "<img class='difficulty' title='" + difficultyRatings[this.get('trailDifficulty')] + "'" +
          " src='../resources/images/trailDifficulties/" + this.get('trailDifficulty') + ".svg' />" +
          "<div class='rating' style='width:" + (this.get('trailRating') / 5) * 80 + "px'>" +
            "<img width='80' src='../resources/images/rating.png'/>" +
          "</div>" +
          "<img width='80' src='../resources/images/ratingGray.png' title='Rating: " + this.get('trailRating') + "'/><br/>" +
          "<strong>Length:</strong> " + this.get('trailLength') + " miles<br/>" +
          "<strong>Condition:</strong> <span title='" + this.get('trailConditionDet') + "'/>" + this.get('trailCondition') + "</span><br/>" +
          "<div style='margin-top: 5px;'>" + this.get('trailSummary') + "</div>" +
        "</div></div>"
      );
      infoWindow.open(map, this);
    });
  }
}

// Draws the route on map showing directions to go from one location to another
function drawRoute() {
  directionsDisplay.setMap(map);

  /******************************* Start Geolocation Determination *******************************/
  var startingLatitudeAsString = document.getElementById("startLat").value.toString();
  var startingLongitudeAsString = document.getElementById("startLong").value.toString();
  var startGeolocation = new google.maps.LatLng(startingLatitudeAsString, startingLongitudeAsString);

  /**************************** Destination Geolocation Determination ****************************/
  var destinationLatitudeAsString = document.getElementById("destinationLat").value.toString();
  var destinationLongitudeAsString = document.getElementById("destinationLong").value.toString();
  var endGeolocation = new google.maps.LatLng(destinationLatitudeAsString, destinationLongitudeAsString);

  /********************************** Travel Mode Determination **********************************/
  var selectedTravelMode = document.getElementById('travelMode').value;

  /***************************** Directions Request Object Creation ******************************/
  var request = {
    origin: startGeolocation,
    destination: endGeolocation,
    travelMode: google.maps.TravelMode[selectedTravelMode]
  };

  /***************************** Obtaining and Displaying Directions *****************************/
  directionsService.route(request, function (response, status) {
    if (status === google.maps.DirectionsStatus.OK) {
      directionsDisplay.setDirections(response);
    }
  });
}

// Helper function to load GPX data onto the map.
function loadGPXFileIntoGoogleMap(map, filepath) {
  $.ajax({url: filepath,
    dataType: "xml",
    success: function (data) {
      var parser = new GPXParser(data, map);
      
      parser.setTrackColour("blue");   // Set the track line colour
      parser.setTrackWidth(8);              // Set the track line width
      parser.setMinTrackPointDelta(0.001);  // Set the minimum distance between track points
      parser.centerAndZoom(data);
      parser.addTrackpointsToMap();         // Add the trackpoints
      
      parser.setTrackColour("#71acf0e6");   // Set the track line colour
      parser.setTrackWidth(5);              // Set the track line width
      parser.setMinTrackPointDelta(0.001);  // Set the minimum distance between track points
      parser.centerAndZoom(data);
      parser.addTrackpointsToMap();         // Add the trackpoints
      parser.addRoutepointsToMap();         // Add the routepoints
      parser.addWaypointsToMap();           // Add the waypoints
    }
  });
}