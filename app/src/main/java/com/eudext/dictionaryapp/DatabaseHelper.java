package com.eudext.dictionaryapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    private String DB_PATH = null;
    private static String DB_NAME = "dictionary.db";
    private SQLiteDatabase myDB;
    private final Context myContext;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
        this.DB_PATH = "/data/data/" + context.getPackageName() + "/" + "databases/";
        Log.e("Path 1", DB_PATH);
    }

    public void createDataBase() throws IOException {
        boolean dbExist = checkDB();
        if(!dbExist) {
            this.getReadableDatabase();
            try{
                copyDatabase();
            }catch (IOException e) {
                throw new Error("Error with coping DB");
            }

        }
    }

    public boolean checkDB() {
        SQLiteDatabase checkDatabase = null;
        try{

            String myPath = DB_PATH + DB_NAME;
            checkDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch (SQLiteException e) {

        }
        if(checkDatabase != null) {
            checkDatabase.close();
        }
        return checkDatabase != null ? true : false;
    }

    private void copyDatabase() throws IOException {

        InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outputFileName = DB_PATH + DB_NAME;
        OutputStream myOutPut = new FileOutputStream(outputFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutPut.write(buffer, 0, length);
        }
        myOutPut.flush();
        myOutPut.close();
        myInput.close();
        Log.i("copyDataBase", "Database Coppied");
    }

    public void openDataBase() throws SQLiteException {

        String myPath = DB_PATH + DB_NAME;
        myDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

    }

    @Override
    public synchronized void close() {
        if(myDB != null) {
            myDB.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String myPAth = DB_PATH + DB_NAME;

       // db = getWritableDatabase();

        String createTableQuery = "CREATE TABLE history(_id, INTEGER PRIMARY KEY AUTOINCREMENT, word TEXT)";

        //createTable(db,  createTableQuery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try{
            this.getReadableDatabase();
            myContext.deleteDatabase(DB_NAME);
            copyDatabase();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Cursor getMeaning(String text) {
        Cursor cursor = myDB.rawQuery("SELECT _id,word,wordtype,definition from meaning where word=='"+text+"'", null);
        return cursor;
    }

    public  Cursor getSuggestions(String text) {
        Cursor cursor = myDB.rawQuery("SELECT _id,word from meaning where word like '"+text+"%' limit 40", null);
        System.out.println(cursor.getCount());
        return cursor;
    }

    public void createTable(SQLiteDatabase db, String query) {
        db.execSQL("CREATE TABLE history(_id INTEGER PRIMARY KEY AUTOINCREMENT, word TEXT)");
    }

    public void insertHistory(String text) {
        myDB.execSQL("INSERT INTO history(word) VALUES('"+text+"')");
    }

    public Cursor getHistoryData() {
        Cursor cursor = myDB.rawQuery("SELECT DISTINCT word from history ORDER BY _id DESC",null);
        return cursor;
    }

    public void deleteHistory() {
        myDB.execSQL("DELETE from history");
    }

}
