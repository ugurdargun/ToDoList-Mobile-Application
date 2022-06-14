package com.msu.todolist;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

public class IntentService_UpdateDB extends IntentService {

    DatabaseHelper dbHelper;
    public IntentService_UpdateDB() {
        super("IntentService_UpdateDB");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        dbHelper = new DatabaseHelper(this);

        if (intent != null) {
            Bundle b = intent.getExtras();
            Task taskItem = b.getParcelable("taskItem");
            boolean res = TaskDB.updateObj(dbHelper, taskItem);

            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtra("editResult", res);
            broadcastIntent.setAction("EDIT_TASK_ACTION");
            sendBroadcast(broadcastIntent);
        }
    }
}