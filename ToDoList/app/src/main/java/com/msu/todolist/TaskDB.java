package com.msu.todolist;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;

public class TaskDB  {
    public static String TABLE_NAME="Tasks";

    public static String TASK_ID = "id";
    public static String TASK_CATEGORY = "taskCategory";
    public static String TASK_NAME = "taskName";
    public static String TASK_DETAILS = "taskDetails";
    public static String TASK_DATE = "taskDate";
    public static String TASK_TIME = "taskTime";
    public static String TASK_FAVORITE = "taskFavorite";
    public static String TASK_STATUS = "taskStatus";

    public static String CREATE_TABLE_SQL="CREATE TABLE "+TABLE_NAME+" ( "+TASK_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+TASK_CATEGORY+" TEXT, "+TASK_NAME+" TEXT, "+ TASK_DETAILS +" TEXT, "+ TASK_DATE +" TEXT, "+TASK_TIME+" TEXT, "+TASK_FAVORITE+" INTEGER DEFAULT 0, "+TASK_STATUS+" INTEGER DEFAULT 0);";
    public static String DROP_TABLE_SQL = "DROP TABLE if exists "+TABLE_NAME;

    public static String ORDER_VALUE = "ASC";
    public static int COMPLETED_VALUE  = 1;
    public static int UNCOMPLETED_VALUE  = 0;
    public static int FAVORITE_VALUE  = 1;

    public static ArrayList<Task> getAllTasks(DatabaseHelper dbHelper){
        Task anItem;
        ArrayList<Task> data = new ArrayList<>();
        String order = TASK_DATE + " " + ORDER_VALUE + ", " + TASK_TIME + " " + ORDER_VALUE;
        Cursor cursor = dbHelper.getAllRecords(TABLE_NAME, null, order);

        while(cursor.moveToNext()){
            int id = cursor.getInt(0);
            String category = cursor.getString(1);
            String name = cursor.getString(2);
            String details = cursor.getString(3);
            String date = cursor.getString(4);
            date = DateConverter.convertDate(date);
            String time = cursor.getString(5);
            time = DateConverter.convertTime(time);
            int favorite = cursor.getInt(6);
            int status = cursor.getInt(7);

            anItem = new Task(id, category, name, details, date, time, favorite, status);
            data.add(anItem);
        }
        return data;
    }

    public static ArrayList<Task> getUncompletedSomeTasks(DatabaseHelper dbHelper, String categoryName){
        Task anItem;
        ArrayList<Task> data = new ArrayList<>();
        String where = TASK_STATUS + " = " + UNCOMPLETED_VALUE + " AND " + TASK_CATEGORY + " = "+ "'"+categoryName+"'";
        String order = TASK_DATE + " " + ORDER_VALUE + ", " + TASK_TIME + " " + ORDER_VALUE;
        Cursor cursor = dbHelper.getSomeRecords(TABLE_NAME, null, where, order);

        while(cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String category = cursor.getString(1);
            String name = cursor.getString(2);
            String details = cursor.getString(3);
            String date = cursor.getString(4);
            date = DateConverter.convertDate(date);
            String time = cursor.getString(5);
            time = DateConverter.convertTime(time);
            int favorite = cursor.getInt(6);
            int status = cursor.getInt(7);

            anItem = new Task(id, category, name, details, date, time, favorite, status);
            if(!DateConverter.pastDateIdentification(anItem.getDate(), anItem.getTime())){
                data.add(anItem);
            }
        }
        return data;
    }

    public static ArrayList<Task> getCompletedTasks(DatabaseHelper dbHelper){
        Task anItem;
        ArrayList<Task> data = new ArrayList<>();
        String where = TASK_STATUS + " = " + COMPLETED_VALUE;
        String order = TASK_DATE + " " + ORDER_VALUE + ", " + TASK_TIME + " " + ORDER_VALUE;
        Cursor cursor = dbHelper.getSomeRecords(TABLE_NAME, null, where, order);

        while(cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String category = cursor.getString(1);
            String name = cursor.getString(2);
            String details = cursor.getString(3);
            String date = cursor.getString(4);
            date = DateConverter.convertDate(date);
            String time = cursor.getString(5);
            time = DateConverter.convertTime(time);
            int favorite = cursor.getInt(6);
            int status = cursor.getInt(7);

            anItem = new Task(id, category, name, details, date, time, favorite, status);
            data.add(anItem);
        }
        return data;
    }

    public static ArrayList<Task> getPlannedFavoriteTasks(DatabaseHelper dbHelper){
        Task anItem;
        ArrayList<Task> data = new ArrayList<>();
        String where =   TASK_STATUS + " = " + UNCOMPLETED_VALUE  +" AND " + TASK_FAVORITE + " = " + FAVORITE_VALUE ;
        String order = TASK_DATE + " " + ORDER_VALUE + ", " + TASK_TIME + " " + ORDER_VALUE;
        Cursor cursor = dbHelper.getSomeRecords(TABLE_NAME, null, where, order);

        while(cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String category = cursor.getString(1);
            String name = cursor.getString(2);
            String details = cursor.getString(3);
            String date = cursor.getString(4);
            date = DateConverter.convertDate(date);
            String time = cursor.getString(5);
            time = DateConverter.convertTime(time);
            int favorite = cursor.getInt(6);
            int status = cursor.getInt(7);

            anItem = new Task(id, category, name, details, date, time, favorite, status);
            if(!DateConverter.pastDateIdentification(anItem.getDate(), anItem.getTime())){
                data.add(anItem);
            }
        }
        return data;
    }

    public static long insert(DatabaseHelper dbHelper, String taskCategory, String taskName, String taskDetails, String taskDate, String taskTime) {
        ContentValues contentValues = new ContentValues( );
        contentValues.put(TASK_CATEGORY, taskCategory);
        contentValues.put(TASK_NAME, taskName);
        contentValues.put(TASK_DETAILS, taskDetails);
        contentValues.put(TASK_DATE, taskDate);
        contentValues.put(TASK_TIME, taskTime);

        long res = dbHelper.insert(TABLE_NAME, contentValues);
        return res;
    }

    public static boolean updateObj(DatabaseHelper dbHelper, Task t) {
        ContentValues contentValues = new ContentValues( );
        contentValues.put(TASK_CATEGORY, t.getCategory());
        contentValues.put(TASK_NAME, t.getName());
        contentValues.put(TASK_DETAILS, t.getDetails());
        contentValues.put(TASK_DATE, t.getDate());
        contentValues.put(TASK_TIME, t.getTime());
        contentValues.put(TASK_STATUS, t.getStatus());

        String where = TASK_ID + " = " + t.getId();
        boolean res = dbHelper.update(TABLE_NAME, contentValues, where);
        return res;
    }

    public static boolean update(DatabaseHelper dbHelper, String taskId, String taskCategory, String taskName, String taskDetails, String taskDate, String taskTime) {
        ContentValues contentValues = new ContentValues( );
        contentValues.put(TASK_CATEGORY, taskCategory);
        contentValues.put(TASK_NAME, taskName);
        contentValues.put(TASK_DETAILS, taskDetails);
        contentValues.put(TASK_DATE, taskDate);
        contentValues.put(TASK_TIME, taskTime);

        String where = TASK_ID + " = " + taskId;
        boolean res = dbHelper.update(TABLE_NAME, contentValues, where);
        return res;
    }

    public static boolean updateFavorite(DatabaseHelper dbHelper, String taskId, String taskFavorite) {
        ContentValues contentValues = new ContentValues( );
        contentValues.put(TASK_FAVORITE, taskFavorite);

        String where = TASK_ID + " = " + taskId;
        boolean res = dbHelper.update(TABLE_NAME, contentValues, where);
        return res;
    }

    public static boolean updateStatus(DatabaseHelper dbHelper, String taskId, String taskStatus) {
        ContentValues contentValues = new ContentValues( );
        contentValues.put(TASK_STATUS, taskStatus);

        String where = TASK_ID + " = " + taskId;
        boolean res = dbHelper.update(TABLE_NAME, contentValues, where);
        return res;
    }

    public static boolean delete(DatabaseHelper dbHelper, String taskId){
        String where = TASK_ID + " = " + taskId;
        boolean res =  dbHelper.delete(TABLE_NAME, where);
        return res;
    }

    public static boolean deleteSelectedTasks(DatabaseHelper dbHelper){
        String where = TASK_STATUS + " = " + "1";
        boolean res =  dbHelper.delete(TABLE_NAME, where);
        return  res;
    }

}