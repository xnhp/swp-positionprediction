package project.software.uni.positionprediction.movebank;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLInput;

import project.software.uni.positionprediction.datatype.Study;

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
            public void handleResponse(String response) {
                if(response == null){

                } else{
                    String data[][] = CSVParser.parseColumnsRows(response);
                    String table[][] = CSVParser.getColumns(data, new String[]{"name", "id", "i_am_owner", "timestamp_end", "timestamp_start"});

                    for(int i = 1; i < table.length; i++){
                        SQLDatabase.getInstance(context).queryWrite(
                                "INSERT OR IGNORE INTO studies (name, id, i_am_owner, timestamp_end, timestamp_start) VALUES ('"
                                        + table[0][i] + "', "
                                        + table[1][i] + ", "
                                        + (table[2][i].equals("false") ? "0" : "1") + ", "
                                        + (table[3][i].equals("") ? "NULL" : table[3][i]) + ", "
                                        + (table[4][i].equals("") ? "NULL" : table[4][i]) + ")");
                    }

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
        String response = MovebankConnector.getInstance(context).getStudiesSync();

        if(response == null){
            Log.e("SQLDatabase", "couldn't fetch studies from Database");
        } else{
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
    }

    /**
     * This method gives all studies in the database
     * @return the Array of studies
     */
    public Study[] getStudies(){

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT id, name FROM studies", new String[]{});

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

}

class SQLDatabaseHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_ENTRIES =
                    "CREATE TABLE studies (" +
                            "name VARCHAR(255)," +
                            "id INT NOT NULL," +
                            "i_am_owner BOOLEAN, " +
                            "timestamp_end TIMESTAMP, " +
                            "timestamp_start TIMESTAMP," +
                            "PRIMARY KEY(id) )";

    private static final String SQL_DELETE_ENTRIES =
                    "DROP TABLE studies";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Movebank.db";

    public SQLDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
