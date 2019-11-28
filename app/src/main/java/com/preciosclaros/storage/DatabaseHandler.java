package com.preciosclaros.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.support.annotation.RequiresApi;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "maprecios_db";

    // Contacts table name
    private static final String TABLE_LIST = "listas";
    private static final String TABLE_PRODUCTS = "productos";

    // Contacts Table Columns names
    private static final String KEY_LIST_ID = "id";
    private static final String KEY_LIST_NAME = "nombre";
    private static final String KEY_LIST_DESCRIPTION = "descripcion";

    private static final String KEY_PROD_ID = "id";
    private static final String KEY_PROD_NAME = "nombre";
    private static final String KEY_PROD_DESCRIPTION = "descripcion";


    private static DatabaseHandler mInstance = null;
    private final Context context;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static synchronized DatabaseHandler getInstance(Context ctx)  {
        if (mInstance == null) {
            mInstance = new DatabaseHandler(ctx.getApplicationContext());
        }
        return mInstance;
    }
    // Creating Tables

    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_LIST + " ("
                + KEY_LIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_LIST_NAME + " TEXT,"
                + KEY_LIST_DESCRIPTION + " TEXT )";

        db.execSQL(CREATE_USER_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        doResetDB(db);
    }

    private void doResetDB(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIST);
        // Create tables again
        onCreate(db);
    }

    /*public void syncUsers(List<User> users){
        if (!users.isEmpty()) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_USER, null, null);
            for(User user : users){
                ContentValues values = new ContentValues();
                values.put(KEY_USER_NAME, user.getName());
                values.put(KEY_USER_CUSUARIO, user.getCusuario());
                values.put(KEY_PASSWORD, user.getPassword());
                values.put(KEY_USER_CODE, user.getCode());

                db.insert(TABLE_USER, null, values);
            }
            db.close();
        }
    }*/
    /*
     public ReporterList getReporterList(){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT "
                + KEY_REPORTER_ID + ","
                + KEY_REPORTER_DESCRIPTION + ","
                + KEY_REPORTER_ID_AIRPORT + ","
                + KEY_REPORTER_DATE_DOWN
                + " FROM " + TABLE_REPORTERS;
        ReporterList reporterList = new ReporterList();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null){
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Reporter r = new Reporter();
                    r.setId(getInt(cursor, 0));
                    r.setDescripcion(getString(cursor, 1));
                    r.setAirportId(getString(cursor, 2));
                    r.setFechaBaja(getString(cursor,3));
                    reporterList.add(r);
                    cursor.moveToNext();
                }
            }
        }
        return reporterList;
    }
    */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean isTableExists(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'";
        try (Cursor cursor = db.rawQuery(query, null)) {
            if(cursor!=null) {
                if(cursor.getCount()>0) {
                    return true;
                }
            }
            return false;
        }
    }
}
