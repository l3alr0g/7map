package com.sevenmap.core.Map;

import java.io.File;

import com.sevenmap.core.Loadable;
import com.sevenmap.data.optiObj.LightObj;
import com.sevenmap.data.osm.OSM;
import com.sevenmap.data.osm.Elements.Bounds.Bounds;

import org.apache.commons.cli.CommandLine;

public class Map extends Loadable {
  private static String defaultMapFileName = "src/main/resources/maps/osm/n7.osm";
  // Default bounds (arround ENSEEIHT school)
  private static Double defaultMinLon = 1.45338;
  private static Double defaultMaxLon = 1.45760;
  private static Double defaultMinLat = 43.60116;
  private static Double defaultMaxLat = 43.60297;

  public void load(CommandLine cl) {

    if (cl.hasOption("B")) {
      // ========================== map loader ==========================
      // This is supposed to be done once, when the user load a new map.
      // ================================================================

      // Create OSM Map
      String fileName = cl.getOptionValue("F", defaultMapFileName);
      Bounds N7Bounds = parseBounds(cl);
      File n7Map = new File(fileName);
      OSM OSMMap = new OSM(N7Bounds, n7Map);

      // Download new map
      OSMMap.downloadMap();

      // Parse data
      OSMMap.parse();

      // Convert into optimized files
      LightObj ltObj = new LightObj(OSMMap);
      ltObj.convertObj();

      // Store this object to the database
      ltObj.storeObj();
    }

  }

  private static Bounds parseBounds(CommandLine cl) {

    // Set default bounds (arround ENSEEIHT school)
    Double minLon = defaultMinLon;
    Double maxLon = defaultMaxLon;
    Double minLat = defaultMinLat;
    Double maxLat = defaultMaxLat;

    // If there is all four options, read them from args
    if (cl.hasOption("minLon") & cl.hasOption("maxLon") & cl.hasOption("minLat") & cl.hasOption("maxLat")) {
      minLon = Double.parseDouble(cl.getOptionValue("minLon", minLon.toString()));
      maxLon = Double.parseDouble(cl.getOptionValue("maxLon", maxLon.toString()));
      minLat = Double.parseDouble(cl.getOptionValue("minLat", minLat.toString()));
      maxLat = Double.parseDouble(cl.getOptionValue("maxLat", maxLat.toString()));
    }

    // Return them
    return new Bounds(minLon, maxLon, minLat, maxLat);
  }

}