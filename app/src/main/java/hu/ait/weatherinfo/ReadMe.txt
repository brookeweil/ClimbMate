  #
  #  README
  #  Final Project - Mobile Software Development
  #  ClimbMate, a Weather App for Going Outdoors
  #
  #     by: Brooke Weil
  #   date: 22 May 2016
  #


  ~~ FEATURES OVERVIEW ~~

    > Add locations by latitude/longitude (manually or by map)
    > Assign custom names to each location
    > See current weather data for each of your saved places
    > See how far you are from each of your saved places
    > Compare temperature now to historical average and to your ideal temps
    > Quickly rule out areas where it’s currently raining


  ~~ TECHNOLOGIES OVERVIEW ~~

    > Google Maps fragments
    > Realm for storage of  places
    > SharedPreferences for storage of ideal temperature
    > OpenWeatherMap API for current weather
    > WeatherUnderground API for historical average weather


  ~~ MAIN SCREEN ~~
  View your saved places and quick info about current conditions, access menu.

  Quickly see current conditions (rain is highlighted in red), whether temperature 
  is especially cool or close to ideal (highlighted in green).


  ~~ MENU DRAWER ~~
  Add a new place, update your ideal weather, refresh the weather for your places,
  view info about the app.


  ~~ ADD A PLACE ~~
  Name your place, set location manually or by choosing a location on a map


  ~~ VIEW PLACE DETAILS ~~
  See all weather details, including historical average max temperature,
  and view your location on a map


  { KNOWN BUGS :( }

     > Distances/locations and showing user's current location are buggy on 
       the emulator
     > Some issues getting the toolbar to show up, decided to forego it and 
       maximize usable screen space