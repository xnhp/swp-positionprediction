package project.software.uni.positionprediction.osm;


import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.tileprovider.modules.IFilesystemCache;
import org.osmdroid.tileprovider.modules.SqlTileWriter;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;

import project.software.uni.positionprediction.R;

public class OSMCacheControl {

    // the tileWriter is the central mechanism for reading/writing/managing data
    public final SqlTileWriter tileWriter;

    // osmDroids cacheManager class provides functionality for downloading tiles
    // some things have to be manually provided i.o.t. be able to instantiate
    // it without a map view.
    // we build ourselves our own CacheManager with our
    // special tileWriter that avoids deletion of tiles.
    public CacheManager cacheManager;
    private Context context;

    public final long maxCacheSize = 600L * 1024 * 1024; // 600MB

    private static OSMCacheControl self;
    private int downloadMinZoom = 5;
    private int downloadMaxZoom = 7;

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
        this.context = ctx;
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        this.tileWriter = new SqlNoDelTileWriter();
        this.cacheManager = buildCacheManager();
    }

    /**
     * Builds a CacheManager without needing a MapView
     * which osmdroid would only use to acess some properties of it
     * we provide these here explicitly
     * @return
     */
    private CacheManager buildCacheManager() {
        ITileSource tileSource = TileSourceFactory.MAPNIK;
        IFilesystemCache fsCache = tileWriter;
        int minZoom = tileSource.getMinimumZoomLevel();
        int maxZoom = tileSource.getMaximumZoomLevel();
        return new CacheManager(tileSource, fsCache, minZoom, maxZoom);
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

    /**
     * Check if cache exceeds a given size
     * @return
     */
    public boolean isCacheTooLarge() {
        return getCacheSize() > maxCacheSize;
    }

    public String getCacheSizeReadable(Context context) {
        String cacheSize = android.text.format.Formatter.formatShortFileSize(context,
                this.getCacheSize());
        return cacheSize;
    }

    /**
     * Saves all zoom levels within range of parameters of a specified region to the cache.
     * osmdroid checks if tiles are already downloaded and does not redownload in that case.
     * (cf. CacheManager.loadTile())
     * By default, osmdroid checks if cached tiles exceed a hardcoded capacity, then removes tiles
     * that are not in or close to the viewport, cf:
     *      - MapTileCache.garbageCollection()    (check & removal)
     *      - MapTileCache constructor            (hardcoded limit)
     * This has been circumvented by using our own custom TileWriter (cf `OSMCacheControl`).
     * @param bbox area to be saved
     */
    public void saveAreaToCache(BoundingBox bbox) {

        cacheManager.downloadAreaAsyncNoUI(context, bbox, downloadMinZoom, downloadMaxZoom, new CacheManager.CacheManagerCallback() {
            @Override
            public void onTaskComplete() {
                Toast.makeText(context, R.string.dialog_map_download_complete, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onTaskFailed(int errors) {
                Toast.makeText(context, "Download complete with " + errors + " errors", Toast.LENGTH_LONG).show();
            }

            @Override
            public void updateProgress(int progress, int currentZoomLevel, int zoomMin, int zoomMax) {
                //NOOP since we are using the built in UI
            }

            @Override
            public void downloadStarted() {
                //NOOP since we are using the built in UI
            }

            @Override
            public void setPossibleTilesInArea(int total) {
                //NOOP since we are using the built in UI
            }
        });
    }

}
