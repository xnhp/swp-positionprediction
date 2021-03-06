package project.software.uni.positionprediction.movebank;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import project.software.uni.positionprediction.controllers.RequestFailedException;
import project.software.uni.positionprediction.datatypes.Bird;
import project.software.uni.positionprediction.datatypes.BirdData;
import project.software.uni.positionprediction.datatypes.HttpStatusCode;
import project.software.uni.positionprediction.datatypes.Location;
import project.software.uni.positionprediction.datatypes.LocationWithValue;
import project.software.uni.positionprediction.datatypes.Locations;
import project.software.uni.positionprediction.datatypes.Request;
import project.software.uni.positionprediction.datatypes.Study;

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
    public float updateBirdDataSync(int studyId, int indivId) throws RequestFailedException {

        if(!updateForBirdNeeded(studyId, indivId)) return 0.0f;

        Request request;
        Date date = getLatestTimestamp(studyId, indivId);

        if(date != null)
            request = MovebankConnector.getInstance(context).getBirdDataSync(studyId, indivId,
                    date, new Date());
        else request = MovebankConnector.getInstance(context).getBirdDataSync(studyId, indivId);

        if(request.getResponseStatus() == HttpStatusCode.OK) {
            return insertBirdData(studyId, indivId, request.getResponse());
        } else {
            Log.e("SQLDatabase", "couldn't fetch data for bird: " + indivId + " from study: " + studyId);
            throw new RequestFailedException();
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

        if(!updateForBirdNeeded(studyId, indivId)) return;

        RequestHandler handler = new RequestHandler() {
            @Override
            public void handleResponse(Request response) {
                if(response.getResponseStatus() == HttpStatusCode.OK){
                    insertBirdData(studyId, indivId, response.getResponse());
                } else {
                    Log.e("SQLDatabase", "couldn't fetch data for bird: " + indivId + " from study: " + studyId);
                }
            }
        };

        Date date = getLatestTimestamp(studyId, indivId);
        if(date != null)
            MovebankConnector.getInstance(context).getBirdData(studyId, indivId,
                    date, new Date(), handler);
        else MovebankConnector.getInstance(context).getBirdData(studyId, indivId, handler);

    }

    /**
     * This Method inserts TrackingPoints into the Database
     * @param response the response of the MovebankRequest to insert
     */
    private float insertBirdData(int studyId, int indivId, String response) {

        if(response.contains("no data available")) return 0.0f;

        String data[][] = CSVParser.parseColumnsRows(response);
        String table[][] = CSVParser.getColumns(data, new String[]{"timestamp", "location_long", "location_lat", "individual_id", "tag_id"});

        SQLiteDatabase db = helper.getWritableDatabase();

        int badDataCounter = 0;

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
                } else badDataCounter++;
            }

            db.execSQL("UPDATE birds SET last_update=DATETIME('now') WHERE study_id=" + studyId + " AND id=" + indivId);
        }

        if(table.length > 0) return ((float)badDataCounter) / ((float)table[0].length);
        return 0.0f;

    }


    /**
     * This methods requests a list of birds for a given study and inserts it into the database.
     * Don't call this Method from Main Thread!
     *
     * @param studyId the id of the study to get the birds for
     * @return 0: OK, 1: accept license needed, 2: no birds for study, -1: error
     */
    public int updateBirdsSync(int studyId){

        Request request = MovebankConnector.getInstance(context).getBirdsSync(studyId);

        if(request.getResponseStatus() == HttpStatusCode.OK) {

                if(request.getResponse().contains("The requested download may contain copyrighted material.")) return 1;
                else if(request.getResponse().contains("No data are available for download")) return 2;

                insertBirds(studyId, request.getResponse());

                return 0;
        } else {
            Log.e("SQLDatabase", "couldn't get birds for study: " + studyId);

            return -1;
        }
    }

    /**
     * This methods requests a list of birds for a given study and inserts it into the database.
     *
     * @param studyId the id of the study to get the birds for
     */
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

    /**
     * This Method parses the given CSV file and inserts it's content into the database
     *
     * @param studyId the studyId of the birds
     * @param response the CSV file as string
     */
    private void insertBirds(int studyId, String response){

        if(response.contains("The requested download may contain copyrighted material.")
                || response.contains("No data are available for download")) return;

        String data[][] = CSVParser.parseColumnsRows(response);
        String table[][] = CSVParser.getColumns(data, new String[]{"comments", "death_comments", "id", "nick_name"});

        SQLiteDatabase db = helper.getWritableDatabase();

        if (table.length > 0) {
            for (int i = 1; i < table[0].length; i++) {
//                db.execSQL("INSERT OR IGNORE INTO birds (comments, death_comments, id, study_id, nick_name) VALUES ("
//                        + (table[0][i].equals("") ? "NULL" : "'" + table[0][i] + "'") + ", "
//                        + (table[1][i].equals("") ? "NULL" : "'" + table[1][i] + "'") + ", "
//                        + table[2][i] + ", "
//                        + studyId + ", "
//                        + (table[3][i].equals("") ? "NULL" : "'" + table[3][i] + "'") + ")");


                SQLiteStatement statement = db.compileStatement("INSERT OR IGNORE INTO birds" +
                        "(comments, death_comments, id, study_id, nick_name) " +
                        "VALUES (?, ?, ?, ?, ?)");

                bindConditionallyNull(table[0][i], statement, 1);
                bindConditionallyNull(table[1][i], statement, 2);
                statement.bindString(3, table[2][i]);
                statement.bindLong(4, studyId); // this is also used for ints
                bindConditionallyNull(table[3][i], statement, 5);

            }

        }

    }

    /**
     * binds the provided value to the placeholder with the specified index
     * in the statement.
     */
    private void bindConditionallyNull(String value, SQLiteStatement target, int index) {
        if (value == null || value.equals("")) {
            target.bindNull(index);
        } else {
            target.bindString(index, value);
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


    /**
     * This Method returns the tracking Data for a given bird
     * @param studyId the study of the bird to get the data for
     * @param indivId the individualId of the bird to get the data for
     * @return the BirdData for the given bird
     */
    public BirdData getBirdData(int studyId, int indivId){

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT timestamp, location_long, location_lat, tag_id, " +
                "individual_id, study_id FROM trackpoints " +
                "WHERE study_id = " + studyId + " AND individual_id = " + indivId +
                " ORDER BY timestamp DESC", new String[]{});


        Locations points = new Locations();


        int rowIndex = 0;
        while(cursor.moveToNext()) {
            points.add( new LocationWithValue<Date>(
                    new Location(cursor.getDouble(1), cursor.getDouble(2)),
                    new Date(Timestamp.valueOf(cursor.getString(0)).getTime())));
            rowIndex++;
        }

        cursor.close();

        return new BirdData(studyId, indivId, points);

    }


    /**
     * This Method returns the tracking Data for a given bird
     * @param studyId the study of the bird to get the data for
     * @param indivId the individualId of the bird to get the data for
     * @return the BirdData for the given bird
     */
    public BirdData getBirdData(int studyId, int indivId, Date dateStart){

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT timestamp, location_long, location_lat, tag_id, " +
                "individual_id, study_id FROM trackpoints " +
                "WHERE study_id = " + studyId + " AND individual_id = " + indivId +
                " AND timestamp >= '" + new Timestamp(dateStart.getTime()).toString() + "' ORDER BY timestamp DESC", new String[]{});
        

        Locations points = new Locations();


        int rowIndex = 0;
        while(cursor.moveToNext()) {
            points.add( new LocationWithValue<Date>(
                    new Location(cursor.getDouble(1), cursor.getDouble(2)),
                    new Date(Timestamp.valueOf(cursor.getString(0)).getTime())));
            rowIndex++;
        }

        cursor.close();

        return new BirdData(studyId, indivId, points);

    }

    public Bird[] getBirds(int studyId){

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, nick_name, favorite, last_update FROM birds WHERE study_id = " +
                        studyId + " ORDER BY nick_name ASC", new String[]{});

        Bird birds[] = new Bird[cursor.getCount()];

        int rowIndex = 0;
        while(cursor.moveToNext()) {
            birds[rowIndex] = new Bird(
                    cursor.getInt(0),
                    studyId,
                    cursor.getString(1),
                    cursor.getString(2).equals("TRUE"),
                    cursor.isNull(3) ? null :
                            new Date(Timestamp.valueOf(cursor.getString(3)).getTime()));
            rowIndex++;
        }

        cursor.close();

        return birds;

    }

    public Bird[] searchBird(int studyId, String searchString){

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, nick_name, favorite, last_update FROM birds WHERE study_id = " +
                        studyId + " AND (nick_name LIKE '%" + searchString + "%' OR id LIKE '%"
                        + searchString + "%') ORDER BY nick_name ASC", new String[]{});

        Bird birds[] = new Bird[cursor.getCount()];

        int rowIndex = 0;
        while(cursor.moveToNext()) {
            birds[rowIndex] = new Bird(
                    cursor.getInt(0),
                    studyId, cursor.getString(1),
                    cursor.getString(2).equals("TRUE"),
                    cursor.isNull(3) ? null :
                            new Date(Timestamp.valueOf(cursor.getString(3)).getTime()));
            rowIndex++;
        }

        cursor.close();

        return birds;

    }

    /**
     * This Method sets the favorite attribute for a given bird to true
     * @param studyId the studyId of the given bird
     * @param individualId the individualId of the given bird
     * @param favorite
     */
    public void setFavorite(int studyId, int individualId, boolean favorite){
        SQLiteDatabase db = helper.getWritableDatabase();

        db.execSQL("UPDATE birds SET favorite=" + (favorite ? "'TRUE'" : "'FALSE'")
                + " WHERE study_id = " + studyId + " AND id = " + individualId);

    }
    public Bird[] getFavorites(){
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, nick_name, study_id, last_update FROM birds WHERE favorite = 'TRUE' ORDER BY study_id", new String[]{});

        Bird birds[] = new Bird[cursor.getCount()];

        /*
            (bm)
            note that in the last_update column, only the
            day is saved (format 2018-06-26).
            for this, i reintroduced my parseDate method.
            It seems ok to me to use the `Timestamp` class
            for the rest.
         */

        int rowIndex = 0;
        while(cursor.moveToNext()) {
            birds[rowIndex] = new Bird(
                    cursor.getInt(0),
                    cursor.getInt(2),
                    cursor.getString(1),
                    true,
                    (cursor.isNull(3) ? null :
                            parseDate(cursor.getString(3))));
            rowIndex++;
        }

        cursor.close();

        return birds;
    }

    public void deleteBirdData(int studyId, int indivId){
        SQLiteDatabase db = helper.getWritableDatabase();

        db.execSQL("DELETE FROM trackpoints WHERE study_id=" + studyId + " AND individual_id=" + indivId);
        db.execSQL("UPDATE birds SET last_update=NULL WHERE study_id=" + studyId + " AND id=" + indivId);
    }

    public void dropAllData(){
        SQLiteDatabase db = helper.getWritableDatabase();

        // WHERE TRUE throws error.
        db.execSQL("DELETE FROM trackpoints");
        db.execSQL("UPDATE birds SET last_update=NULL");
    }

    /**
     * This method checks weather the difference between the latest entry for a given bird and the
     * current time is bigger than the difference between the last two timestamps.
     * @param studyId The id of the study the bird belongs to
     * @param indivId the individual id of the bird
     * @return true if an update is needed
     */
    private boolean updateForBirdNeeded(int studyId, int indivId){

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT timestamp FROM trackpoints WHERE individual_id = "
                + studyId + " AND individual_id = "
                + indivId+ " ORDER BY timestamp DESC LIMIT 2", new String[]{});

        if(cursor.getCount() < 2){
            return true;
        }

        cursor.moveToNext();
        Date date1 = new Date(Timestamp.valueOf(cursor.getString(0)).getTime());
        cursor.moveToNext();
        Date date2 = new Date(Timestamp.valueOf(cursor.getString(0)).getTime());

        cursor.close();

        long step = date1.getTime() - date2.getTime();

        Date now = new Date();

        long diff = now.getTime() - date1.getTime();

        return step < diff;

    }


    /**
     * This Method returns the latest timestamp in the database
     * @param studyId
     * @param indivId
     * @return
     */
    private Date getLatestTimestamp(int studyId, int indivId){
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT timestamp FROM trackpoints WHERE study_id = "
                + studyId + " AND individual_id = "
                + indivId+ " ORDER BY timestamp DESC LIMIT 1", new String[]{});

        if(cursor.getCount() >= 1){
            cursor.moveToNext();
            Timestamp timestamp = Timestamp.valueOf(cursor.getString(0));
            Date date = new Date(timestamp.getTime());
            cursor.close();
            return date;
        }

        return null;

    }


    /**
     * Parses a date from string.
     * @param dateString of the pattern 2008-05-31 19:30:18.998 or 2008-05-31
     * @return a Date object corresponding to the parsed date
     */
    private Date parseDate(String dateString) {
        // cf https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
        DateFormat dfPrecise = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
        DateFormat dfDate = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date date = null;
        try {
            date = dfPrecise.parse(dateString);
        } catch (ParseException e) {

            e.printStackTrace();
            try {
                date = dfDate.parse(dateString);
            } catch (ParseException e1) {
                Log.e("SQLDatabase", "Could not parse date of row");
                e1.printStackTrace();
            }
        }
        Log.i("SQLDatabase", "retrieved date from db: " + date);
        return date;
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
                            "nick_name VARCHAR," +
                            "favorite BOOLEAN DEFAULT FALSE, " +
                            "last_update TIMESTAMP DEFAULT NULL, " +
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
