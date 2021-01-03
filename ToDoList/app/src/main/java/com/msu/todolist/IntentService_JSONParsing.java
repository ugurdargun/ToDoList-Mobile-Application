package com.msu.todolist;

import android.app.IntentService;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class IntentService_JSONParsing extends IntentService {
    String jsonStr;
    JSONObject categoryJSONObj;
    JSONArray taskCats;

    public static final String TAG_TASKCATS = "taskCategories";
    public IntentService_JSONParsing() {
        super("JsonParsingIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        parseTaskCategories();
    }

    public void parseTaskCategories() {

        jsonStr = loadFileFromAssets("categories.json");

        if (jsonStr != null) {
            try {
                categoryJSONObj = new JSONObject(jsonStr);
                taskCats = categoryJSONObj.getJSONArray(TAG_TASKCATS);
                ArrayList<Category> catList = new ArrayList<>();

                for (int i = 0; i < taskCats.length(); i++) {
                    JSONObject jsonObj = taskCats.getJSONObject(i);
                    String category_name = jsonObj.getString("name");
                    String category_imgName = jsonObj.getString("imgName");
                    Category cat = new Category(category_name, category_imgName);
                    catList.add(cat);
                }

                Intent broadcastIntent = new Intent();
                broadcastIntent.putParcelableArrayListExtra("categoryList", catList);
                broadcastIntent.setAction("JSON_PARSE_COMPLETED_ACTION");
                getBaseContext().sendBroadcast(broadcastIntent);

            } catch (JSONException ee) {
                ee.printStackTrace();
            }
        }
    }

    private String loadFileFromAssets(String fileName) {
        String file = null;
        try {
            InputStream is = getBaseContext().getAssets().open(fileName);

            int size = is.available();
            byte[] buffer = new byte[size];

            is.read(buffer);
            is.close();

            file = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return file;
    }

}
