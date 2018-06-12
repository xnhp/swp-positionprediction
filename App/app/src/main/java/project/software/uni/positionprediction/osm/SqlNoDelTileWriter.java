package project.software.uni.positionprediction.osm;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.util.Log;

import org.osmdroid.api.IMapView;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.modules.DatabaseFileArchive;
import org.osmdroid.tileprovider.modules.SqlTileWriter;
import org.osmdroid.tileprovider.modules.SqliteArchiveTileWriter;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.util.StreamUtils;
import org.osmdroid.util.MapTileIndex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * We dont subclass SqliteArchiveTileWriter because then we wouldnt
 * be able to remove any data since it's `db` field is not public.
 * (only other way would be to copy entire class code)
 * Instead, we do it our own way and subclass SqlTileWriter instead and
 * prevent deletion of tiles by garbage collection or such by overriding
 * the relevant methods with noops.
 */
public class SqlNoDelTileWriter extends org.osmdroid.tileprovider.modules.SqlTileWriter {

    @Override
    public boolean remove(final ITileSource tileSource, final long pMapTileIndex) {
        return false;
    }

    @Override
    public Long getExpirationTimestamp(final ITileSource pTileSource, final long pMapTileIndex) {
        return null;
    }

    @Override
    public void runCleanupOperation() {
        // noop
    }

    // parent already exhibits a purgeCache method
}
