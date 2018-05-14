package project.software.uni.positionprediction.osm;

import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;

/**
 * This class stores basic configuration values for any OSMDroid map.
 * TODO: move strings to XML files
 */
final class OSMDroidMapConfiguration {
    static OnlineTileSourceBase onlineTileSourceBase = TileSourceFactory.MAPNIK;
    // TODO: Put this into a xml file
    public static String downloadCompleteText = "Download fertig!";
    public static String dialogPermissionStorageText = "Die App benötigt die Berechtigung auf den Speicher zuzugreifen um Kartendaten offline speichern zu können";
}
