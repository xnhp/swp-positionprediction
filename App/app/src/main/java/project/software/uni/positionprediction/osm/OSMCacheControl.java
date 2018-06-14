package project.software.uni.positionprediction.osm;


import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.modules.SqlTileWriter;

public class OSMCacheControl {

    // the tileWriter is the central mechanism for reading/writing/managing data
    public final SqlTileWriter tileWriter;
    // private Context context;

    public final long maxCacheSize = 600L * 1024 * 1024; // 600MB

    private static OSMCacheControl self;

    /**
     * note: a context is required for initialisation, however
     * it is discarded afterwards.
     * @param ctx
     * @return
     */
    public static OSMCacheControl getInstance(Context ctx) {
        if (self == null) {
            self = new OSMCacheControl(ctx);
        }
        return self;
    }

    public OSMCacheControl(Context ctx) {
        // context is only used for this
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
        Log.i("OSMCacheControl", "clear cache triggered");
        tileWriter.purgeCache();
    }

    public boolean isCacheTooLarge() {
        //return getCacheSize() > maxCacheSize;
        return getCacheSize() > maxCacheSize;
    }

    public String getCacheSizeReadable(Context context) {
        String cacheSize = android.text.format.Formatter.formatShortFileSize(context,
                this.getCacheSize());
        return cacheSize;
    }

}
