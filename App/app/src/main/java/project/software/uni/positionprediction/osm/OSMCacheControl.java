package project.software.uni.positionprediction.osm;


import android.content.Context;
import android.preference.PreferenceManager;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.modules.SqlTileWriter;

public class OSMCacheControl {

    // the tileWriter is the central mechanism for reading/writing/managing data
    public final SqlTileWriter tileWriter;

    public OSMCacheControl(Context ctx) {
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        this.tileWriter = new SqlNoDelTileWriter();
    }

    /**
     * Retrieve the amount of currently occupied space in bytes.
     * Can be converted into a human-readable string with e.g. https://stackoverflow.com/a/26502430/156884
     */
    public long getCacheSize() {
       return tileWriter.getSize();
    }

    /**
     * Clear the entire cache
     * cf. https://github.com/osmdroid/osmdroid/blob/master/OpenStreetMapViewer/src/main/java/org/osmdroid/samplefragments/cache/CachePurge.java
     */
    public void clearCache() {
        tileWriter.purgeCache();
    }

}
