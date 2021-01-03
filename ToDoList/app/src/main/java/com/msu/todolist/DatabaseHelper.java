package com.msu.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "TaskDB";
    public static int DATABASE_VERSION = 3;
    SQLiteDatabase db;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION );
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(TaskDB.CREATE_TABLE_SQL);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(TaskDB.DROP_TABLE_SQL);
        }catch (SQLException e){
            e.printStackTrace();
        }
        onCreate(db);
    }

    public Cursor getAllRecords(String tableName, String[] columns, String orderBy){
        Cursor cursor = db.query(tableName, columns, null, null, null, null, orderBy);
        return cursor;
    }
    public Cursor getSomeRecords( String tableName, String[] columns, String whereCondition, String orderBy){
        Cursor cursor = db.query(tableName, columns, whereCondition, null, null, null, orderBy);
        return cursor;
    }

    public long insert( String tableName, ContentValues contentValues){
        return db.insert(tableName, null, contentValues);
    }
    public boolean update(String tableName, ContentValues contentValues, String whereCondition) {
        return db.update(tableName, contentValues, whereCondition,null)>0;
    }
    public boolean delete(String tableName, String whereCondition) {
        return db.delete(tableName, whereCondition, null)>0;
    }
}