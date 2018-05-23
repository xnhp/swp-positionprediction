package project.software.uni.positionprediction.movebank;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by simon on 22.05.18.
 */

public class SQLDatabase {

    static SQLDatabase instance = null;

    private SQLDatabaseHelper helper;

    private SQLDatabase(Context context) {
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

        Cursor cursor = db.rawQuery(query, null);
        cursor.close();
    }

    public String[][] queryRead(String query){

        SQLiteDatabase db = helper.getWritableDatabase();

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
