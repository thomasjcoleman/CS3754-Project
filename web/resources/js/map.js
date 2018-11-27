/* 
 * Created by Thomas Coleman on 2018.11.15
 * Copyright © 2018 Thomas Coleman. All rights reserved. * 
 */

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
    mapTypeId: google.maps.MapTypeId.ROADMAP
  });
  display();
}

// Prepare the map object as necessary
function display() {
  // show user's position
  navigator.geolocation.getCurrentPosition(function(pos) {
    var c = pos.coords;
    var marker = new google.maps.Marker({
      position: new google.maps.LatLng(c.latitude, c.longitude),
      title: "Your location",
      icon: "../resources/images/userLocation.png",
      map: map
    });
  });
  
  if (document.getElementById("trailName") !== null) {
    // If there is a specific trail named, only show that one.
    displaySingleTrail();
  } else {
    // Else, show all trails.
    displayAllTrails();
  }

}


// Displays the geolocation of a single trail.
// TODO: add trail GPX file
function displaySingleTrail() {
  // Get the trail's information
  var trailName = document.getElementById("trailName").value;
  var trailLatitude = document.getElementById("trailLat").value;
  var trailLongitude = document.getElementById("trailLon").value;

  // Center the map on that trail
  var trailLatLong = new google.maps.LatLng(trailLatitude, trailLongitude);
  map.setCenter(trailLatLong);

  // Add a new pin marker with the trail's properties
  currentMarker = new google.maps.Marker({
    title: trailName,
    position: trailLatLong,
    map: map
  });
  currentMarker.setMap(map);

  // Instantiate a new info window for the marker and attach it
  var infoWindow = new google.maps.InfoWindow();
  google.maps.event.addListener(currentMarker, "click", function () {
    infoWindow.setContent(this.get('title'));
    infoWindow.open(map, this);
  });
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
      trailImage: trail.imgMedium || "../resources/images/noImage.jpg",
      trailDifficulty: trail.difficulty,
      trailRating: (trail.stars / 5) * 80,
      trailLength: trail.length,
      trailSummary: trail.summary
    });
    marker.setMap(map);

    google.maps.event.addListener(marker, "click", function () {
      infoWindow.setContent(
        "<h1>" + this.get('title') + "</h1>" +
        "<div style='overflow: auto'><div class='float50'>" +
          "<img class='preview' src='" + this.get('trailImage') + "'/><br/>" +
        "</div><div class='float50'>" +
          "<img class='difficulty' title='Difficulty'" +
            " src='../resources/images/trailDifficulties/" + this.get('trailDifficulty') + ".svg' />" +
          "<div class='rating' style='width:" + this.get('trailRating') + "px'>" +
            "<img width='80' src='../resources/images/rating.png'/>" +
          "</div>" +
          "<img width='80' src='../resources/images/ratingGray.png'/><br/>" +
          "<strong>Length:</strong> " + this.get('trailLength') + " miles<br/>" +
        "</div></div>" +
        trail.summary
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
