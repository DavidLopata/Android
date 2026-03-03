This is my first ever Android aplication,it is a Campus Navigation app meant for 
students of the Ss. Cyril and Methodius University in Skopje,where thay can 
easily navigate their respected campuses.

Currently some locations of the following faculties are implemented:
-Faculty of Electrical Engineering and Information Technologies
-Faculty of Civil Engineering
-Faculty of Medicine

The way that the app is constructed,this list can easily be expanded on with
new locations for existing faculties,as well as completely new faculties.


## Setup

This project uses Google Maps API. To run the map fully:

1. Get your own Google Maps API key.
2. Open `AndroidManifest.xml` and replace:

   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="insert your map API key here"/>
