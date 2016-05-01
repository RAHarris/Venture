package descriptio.net.venture.io;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by raharri on 4/25/2016.
 */
public class PeriegesisDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Periegesis.db";

    private final String LOGCAT_TAG = "PeriegesisDbHelper";

    public PeriegesisDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(LOGCAT_TAG, "creating database");
        db.execSQL(Commands.SQL_CREATE_ENTRIES);
        Log.i(LOGCAT_TAG, "successfully created db");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: figure out db upgrade path
    }

    public long addAstu(String filepath, AstuStateContract.LocTypes locType) {
        // TODO: make async
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AstuStateContract.AstuState.COLUMN_NAME_FILEPATH, filepath);
        values.put(AstuStateContract.AstuState.COLUMN_NAME_LOC_TYPE, locType.ordinal());
        Log.i(LOGCAT_TAG, values.toString());
        long newRowId;
        newRowId = db.insert(
                AstuStateContract.AstuState.TABLE_NAME,
                null,
                values
        );
        return newRowId;
    }

    public String[] getAstuDetails(long id) {
        // TODO: make async & error handling
        String[] path = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                AstuStateContract.AstuState._ID,
                AstuStateContract.AstuState.COLUMN_NAME_FILEPATH,
                AstuStateContract.AstuState.COLUMN_NAME_LOC_TYPE,
        };
        String whereClause = AstuStateContract.AstuState._ID + " =?";
        String [] whereArgs = { Long.toString(id) };
        Cursor cursor = db.query(
                AstuStateContract.AstuState.TABLE_NAME,
                projection,
                whereClause,
                whereArgs,
                null,
                null,
                AstuStateContract.AstuState._ID);
        if (cursor.moveToFirst()) {
            path = new String[] {
                    cursor.getString(cursor.getColumnIndex(AstuStateContract.AstuState.COLUMN_NAME_FILEPATH)),
                    cursor.getString(cursor.getColumnIndex(AstuStateContract.AstuState.COLUMN_NAME_LOC_TYPE)),
            };
        }
        return path;
    }

    public String getIdForPath(String filename) {
        // TODO: make async & error handling
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                AstuStateContract.AstuState._ID,
                AstuStateContract.AstuState.COLUMN_NAME_FILEPATH,
        };
        String whereClause = AstuStateContract.AstuState.COLUMN_NAME_FILEPATH + " =?";
        String [] whereArgs = { filename };
        Cursor cursor = db.query(
                AstuStateContract.AstuState.TABLE_NAME,
                projection,
                whereClause,
                whereArgs,
                null,
                null,
                AstuStateContract.AstuState._ID);
        cursor.moveToFirst();
        String path = cursor.getString(
                cursor.getColumnIndex(AstuStateContract.AstuState._ID)
        );
        return path;
    }

    public List<String[]> getAsteaDetails() {
        // TODO: make async & error handling
        SQLiteDatabase db = this.getReadableDatabase();
        List<String[]> paths = new ArrayList<>();
        String[] projection = {
                AstuStateContract.AstuState._ID,
                AstuStateContract.AstuState.COLUMN_NAME_FILEPATH,
                AstuStateContract.AstuState.COLUMN_NAME_LOC_TYPE,
        };
        String whereClause = AstuStateContract.AstuState._ID + " !=?";
        String [] whereArgs = { Integer.toString(-1) };
        Cursor cursor = db.query(
                AstuStateContract.AstuState.TABLE_NAME,
                projection,
                whereClause,
                whereArgs,
                null,
                null,
                AstuStateContract.AstuState._ID);
        if (cursor.moveToFirst()) {
            String[] first = {
                    cursor.getString(cursor.getColumnIndex(AstuStateContract.AstuState._ID)),
                    cursor.getString(cursor.getColumnIndex(AstuStateContract.AstuState.COLUMN_NAME_FILEPATH)),
                    cursor.getString(cursor.getColumnIndex(AstuStateContract.AstuState.COLUMN_NAME_LOC_TYPE))
            };
            paths.add(first);
        }
        while (cursor.moveToNext()) {
            paths.add(new String[]{
                    cursor.getString(cursor.getColumnIndex(AstuStateContract.AstuState._ID)),
                    cursor.getString(cursor.getColumnIndex(AstuStateContract.AstuState.COLUMN_NAME_FILEPATH)),
                    cursor.getString(cursor.getColumnIndex(AstuStateContract.AstuState.COLUMN_NAME_LOC_TYPE))
            });
        }
        return paths;
    }

    public List<String> getAsteaPathnames() {
        // TODO: make async & error handling
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> pathnames = new ArrayList<>();
        String[] projection = {
                AstuStateContract.AstuState._ID,
                AstuStateContract.AstuState.COLUMN_NAME_FILEPATH,
        };
        String whereClause = AstuStateContract.AstuState._ID + " !=?";
        String [] whereArgs = { Integer.toString(-1) };
        Cursor cursor = db.query(
                AstuStateContract.AstuState.TABLE_NAME,
                projection,
                whereClause,
                whereArgs,
                null,
                null,
                AstuStateContract.AstuState._ID);
        if (cursor.moveToFirst()) {
            pathnames.add(cursor.getString(cursor.getColumnIndex(AstuStateContract.AstuState.COLUMN_NAME_FILEPATH)));
        }
        while (cursor.moveToNext()) {
            pathnames.add(cursor.getString(cursor.getColumnIndex(AstuStateContract.AstuState.COLUMN_NAME_FILEPATH)));
        }
        return pathnames;
    }

    private static class Commands {
        private static final String TEXT_TYPE = " TEXT";
        private static final String INT_TYPE = " INTEGER";
        private static final String COMMA_SEP = ",";
        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + AstuStateContract.AstuState.TABLE_NAME + " (" +
                        AstuStateContract.AstuState._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        AstuStateContract.AstuState.COLUMN_NAME_FILEPATH + TEXT_TYPE + COMMA_SEP +
                        AstuStateContract.AstuState.COLUMN_NAME_LOC_TYPE + INT_TYPE +
                " )";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + AstuStateContract.AstuState.TABLE_NAME;
    }
}
