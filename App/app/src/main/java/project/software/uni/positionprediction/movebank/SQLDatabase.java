package project.software.uni.positionprediction.movebank;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Date;

import project.software.uni.positionprediction.datatype.Bird;
import project.software.uni.positionprediction.datatype.BirdData;
import project.software.uni.positionprediction.datatype.HttpStatusCode;
import project.software.uni.positionprediction.datatype.Location2D;
import project.software.uni.positionprediction.datatype.Request;
import project.software.uni.positionprediction.datatype.Study;
import project.software.uni.positionprediction.datatype.TrackingPoint;

/**
 * Created by simon on 22.05.18.
 */

public class SQLDatabase {

    private Context context;

    static SQLDatabase instance = null;

    private SQLDatabaseHelper helper;

    private SQLDatabase(Context context) {
        this.context = context;
        helper = new SQLDatabaseHelper(context);
    }

    public static SQLDatabase getInstance(Context context){
        if(instance == null){
            return instance = new SQLDatabase(context);
        }
        return instance;
    }

    public void queryWrite(String query){
        SQLiteDatabase db = helper.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, new String[]{});
        cursor.close();
    }

    public String[][] queryRead(String query){

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        String result[][] = new String[cursor.getCount()+1][];

        result[0] = new String[cursor.getColumnCount()];
        for(int i = 0; i < cursor.getColumnCount(); i++){
            result[0][i] = cursor.getColumnName(i);
        }

        int rowIndex = 1;
        while(cursor.moveToNext()) {
            result[rowIndex] = new String[cursor.getColumnCount()];
            for(int i = 0; i < cursor.getColumnCount(); i++){
                result[rowIndex][i] = cursor.getString(i);
            }
        }

        cursor.close();

        return result;
    }


    /**
     * This Method requests a list of studies from the movebank and inserts them into the database
     */
    public void updateStudies(){
        MovebankConnector.getInstance(context).getStudies(new RequestHandler() {
            @Override
            public void handleResponse(Request response) {
                if(response.getResponseStatus() == HttpStatusCode.OK) {
                    insertStudyData(response.getResponse());
                }


            }
        });
    }

    /**
     * This Method requests a list of studies from the movebank and inserts them into the database
     *
     * Don't call this Method from the main Thread!
     */
    public void updateStudiesSync(){

        Request request = MovebankConnector.getInstance(context).getStudiesSync();

        if(request.getResponseStatus() == HttpStatusCode.OK) {
            insertStudyData(request.getResponse());
        } else {
            Log.e("SQLDatabase", "couldn't fetch studies from Database");
        }
    }

    /**
     * This method inserts studies into the database
     * @param response the response from the MovebankConnector
     */
    private void insertStudyData(String response){
        String data[][] = CSVParser.parseColumnsRows(response);
        String table[][] = CSVParser.getColumns(data, new String[]{"name", "id", "i_am_owner", "timestamp_end", "timestamp_start"});

        SQLiteDatabase db = helper.getWritableDatabase();

        if(table.length > 0) {
            for (int i = 1; i < table[0].length; i++) {
                db.execSQL( "INSERT OR IGNORE INTO studies (name, id, i_am_owner, timestamp_end, timestamp_start) VALUES ('"
                        + table[0][i].replace("\'", "''") + "', "
                        + table[1][i] + ", "
                        + (table[2][i].equals("false") ? "0" : "1") + ", "
                        + (table[3][i].equals("") ? "NULL" : "'" + table[3][i] + "'") + ", "
                        + (table[4][i].equals("") ? "NULL" : "'" + table[4][i] + "'") + ")");
            }
        }
    }


    /**
     * This Method requests the tracking points for a given bird from the movebank and inserts them
     * into the database
     *
     * Don't call this Method from the main Thread!
     *
     * @param studyId The id of the study the bird belongs to
     * @param indivId The id of the bird
     */
    public void updateBirdDataSync(int studyId, int indivId){

        Request request = MovebankConnector.getInstance(context).getBirdDataSync(studyId, indivId);

        if(request.getResponseStatus() == HttpStatusCode.OK) {
            insertBirdData(studyId, indivId, request.getResponse());
        } else {
            Log.e("SQLDatabase", "couldn't fetch data for bird: " + indivId + " from study: " + studyId);
        }

    }

    /**
     * This Method requests the tracking points for a given bird from the movebank and inserts them
     * into the database
     *
     * @param studyId The id of the study the bird belongs to
     * @param indivId The id of the bird
     */
    public void updateBirdData(final int studyId, final int indivId){

        MovebankConnector.getInstance(context).getBirdData(studyId, indivId, new RequestHandler() {
            @Override
            public void handleResponse(Request response) {
                if(response.getResponseStatus() == HttpStatusCode.OK){
                    insertBirdData(studyId, indivId, response.getResponse());
                } else {
                    Log.e("SQLDatabase", "couldn't fetch data for bird: " + indivId + " from study: " + studyId);
                }
            }
        });
    }

    /**
     * This Method inserts TrackingPoints into the Database
     * @param response the response of the MovebankRequest to insert
     */
    private void insertBirdData(int studyId, int indivId, String response) {

        if(response.contains("no data available")) return;

        String data[][] = CSVParser.parseColumnsRows(response);
        String table[][] = CSVParser.getColumns(data, new String[]{"timestamp", "location_long", "location_lat", "individual_id", "tag_id"});

        SQLiteDatabase db = helper.getWritableDatabase();

        if (table.length > 0) {
            for (int i = 1; i < table[0].length; i++) {
                if (!table[1][i].equals("") && !table[2][i].equals("")) {
                    db.execSQL("INSERT OR IGNORE INTO trackpoints (timestamp, location_long, location_lat, tag_id, individual_id, study_id) VALUES ("
                            + (table[0][i].equals("") ? "NULL" : "'" + table[0][i] + "'") + ", "
                            + table[1][i] + ", "
                            + table[2][i] + ", "
                            + (table[4][i].equals("") ? "NULL" : table[4][i]) + ", "
                            + table[3][i] + ", "
                            + studyId + ")");
                }
            }

        }
    }


    public void updateBirdsSync(int studyId){

        Request request = MovebankConnector.getInstance(context).getBirdsSync(studyId);

        if(request.getResponseStatus() == HttpStatusCode.OK) {
                insertBirds(studyId, request.getResponse());
        } else {
            Log.e("SQLDatabase", "couldn't get birds for study: " + studyId);
        }
    }

    public void updateBirds(final int studyId){
        MovebankConnector.getInstance(context).getBirds(studyId, new RequestHandler() {
            @Override
            public void handleResponse(Request response) {
                if(response.getResponseStatus() == HttpStatusCode.OK){
                    insertBirds(studyId, response.getResponse());
                } else {
                    Log.e("SQLDatabase", "couldn't birds for study: " + studyId);
                }
            }
        });
    }

    private void insertBirds(int studyId, String response){

        if(response.contains("The requested download may contain copyrighted material.")
                || response.contains("No data are available for download")) return;

        String data[][] = CSVParser.parseColumnsRows(response);
        String table[][] = CSVParser.getColumns(data, new String[]{"comments", "death_comments", "id", "nick_name"});

        SQLiteDatabase db = helper.getWritableDatabase();

        if (table.length > 0) {
            for (int i = 1; i < table[0].length; i++) {
                db.execSQL("INSERT OR IGNORE INTO birds (comments, death_comments, id, study_id, nick_name) VALUES ("
                        + (table[0][i].equals("") ? "NULL" : "'" + table[0][i] + "'") + ", "
                        + (table[1][i].equals("") ? "NULL" : "'" + table[1][i] + "'") + ", "
                        + table[2][i] + ", "
                        + studyId + ", "
                        + (table[3][i].equals("") ? "NULL" : "'" + table[3][i] + "'") + ")");
            }

        }
    }

    /**
     * This method gives all studies in the database
     * @return the Array of studies
     */
    public Study[] getStudies(){

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT id, name FROM studies ORDER BY name ASC", new String[]{});

        Study result[] = new Study[cursor.getCount()];

        int rowIndex = 0;
        while(cursor.moveToNext()) {
            result[rowIndex] = new Study();
            result[rowIndex].id = cursor.getInt(0);
            result[rowIndex].name = cursor.getString(1);
            rowIndex++;
        }

        cursor.close();

        return result;

    }

    public Study[] searchStudie(String searchString){

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT id, name FROM studies WHERE name LIKE '%"
                + searchString +"%' OR id LIKE '%" + searchString + "%' ORDER BY name ASC",
                new String[]{});

        Study result[] = new Study[cursor.getCount()];

        int rowIndex = 0;
        while(cursor.moveToNext()) {
            result[rowIndex] = new Study();
            result[rowIndex].id = cursor.getInt(0);
            result[rowIndex].name = cursor.getString(1);
            rowIndex++;
        }

        cursor.close();

        return result;

    }


    public BirdData getBirdData(int studyId, int indivId){

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT timestamp, location_long, location_lat, tag_id, " +
                "individual_id, study_id FROM trackpoints WHERE study_id = " + studyId +
                " AND individual_id = " + indivId + " ORDER BY timestamp DESC", new String[]{});

        TrackingPoint points[] = new TrackingPoint[cursor.getCount()];

        int rowIndex = 0;
        while(cursor.moveToNext()) {
            points[rowIndex] = new TrackingPoint(
                    new Location2D(cursor.getDouble(1), cursor.getDouble(2)),
                    new Date(cursor.getLong(0)*1000));
            rowIndex++;
        }

        cursor.close();


        return new BirdData(studyId, indivId, points);

    }

    public Bird[] getBirds(int studyId){

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, nick_name FROM birds WHERE study_id = " +
                        studyId + " ORDER BY nick_name ASC", new String[]{});

        Bird birds[] = new Bird[cursor.getCount()];

        int rowIndex = 0;
        while(cursor.moveToNext()) {
            birds[rowIndex] = new Bird(cursor.getInt(0), studyId, cursor.getString(1));
            rowIndex++;
        }

        cursor.close();


        return birds;

    }

    public Bird[] searchBird(int studyId, String searchString){

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, nick_name FROM birds WHERE study_id = " +
                        studyId + " AND (nick_name LIKE '%" + searchString + "%' OR id LIKE '%"
                        + searchString + "%') ORDER BY nick_name ASC", new String[]{});

        Bird birds[] = new Bird[cursor.getCount()];

        int rowIndex = 0;
        while(cursor.moveToNext()) {
            birds[rowIndex] = new Bird(cursor.getInt(0), studyId, cursor.getString(1));
            rowIndex++;
        }

        cursor.close();


        return birds;

    }

}

class SQLDatabaseHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_STUDIES =
                    "CREATE TABLE studies (" +
                            "name VARCHAR(255)," +
                            "id INT NOT NULL," +
                            "i_am_owner BOOLEAN, " +
                            "timestamp_end TIMESTAMP, " +
                            "timestamp_start TIMESTAMP," +
                            "PRIMARY KEY(id) ); ";
    private static final String SQL_CREATE_TRACKPOINTS =
                    "CREATE TABLE trackpoints (" +
                            "timestamp TIMESTAMP," +
                            "location_long DOUBLE," +
                            "location_lat DOUBLE," +
                            "individual_id INTEGER," +
                            "tag_id INTEGER," +
                            "study_id INTEGER, " +
                            "PRIMARY KEY(timestamp, individual_id, study_id) );";
    private static final String SQL_CREATE_BIRDS =
                    "CREATE TABLE birds (" +
                            "comments VARCHAR, " +
                            "death_comments VARCHAR, " +
                            "id INTEGER, " +
                            "study_id INTEGER, " +
                            "nick_name VARCHAR, " +
                            "PRIMARY KEY(id) );";

    private static final String SQL_DELETE_STUDIES = "DROP TABLE studies";
    private static final String SQL_DELETE_TRACKPOINTS = "DROP TABLE trackpoints";
    private static final String SQL_DELETE_BIRDS = "DROP TABLE birds;";


    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Movebank.db";

    public SQLDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_STUDIES);
        db.execSQL(SQL_CREATE_TRACKPOINTS);
        db.execSQL(SQL_CREATE_BIRDS);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_STUDIES);
        db.execSQL(SQL_DELETE_TRACKPOINTS);
        db.execSQL(SQL_DELETE_BIRDS);

        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
