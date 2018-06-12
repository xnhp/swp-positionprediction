package project.software.uni.positionprediction.controllers;

import android.content.Context;

import org.osmdroid.tileprovider.modules.SqlTileWriter;

import project.software.uni.positionprediction.activities.Settings;
import project.software.uni.positionprediction.osm.OSMCacheControl;
import project.software.uni.positionprediction.osm.SqlNoDelTileWriter;

/**
 * rudimentary implementation of a controller that handles the
 * coordination between different activites and what is shown/done etc
 *
 * intended to be initialised as a singleton at start of the app.
 *
 * all this will probably be integrated into a proper thing later.
 */
public class ModeController {

    /*
    problem:
    in order to manage cache (e.g. from settings view),
    how can i have methods of OSMDroidMap available before Ive ever been in the
    activity (where the map is displayed)
    (cannot initiate most of it until the view is available)

    potential solution:
    we can indeed initiate the tileWriter beforehand!
     */

    private static ModeController self;
    //private static Context context;


    public static ModeController getInstance(Context ctx) {
        if (self == null) {
            self = new ModeController(ctx);
        }
        return self;
    }


    public ModeController(Context ctx) {
        this.osmCacheControl = new OSMCacheControl(ctx);
    }

    public OSMCacheControl osmCacheControl;

    public SqlTileWriter getOSMTileWriter() {
        return this.osmCacheControl.tileWriter;
    }


}
