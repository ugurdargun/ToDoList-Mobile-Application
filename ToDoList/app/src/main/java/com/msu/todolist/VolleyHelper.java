package com.msu.todolist;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.msu.todolist.Activities.AboutActivity;

public class VolleyHelper {
    RequestQueue queue;
    Context context;

    public VolleyHelper(Context context){
        this.context =context;
        queue = Volley.newRequestQueue(context);
    }

    public void requestForBinaryData(String urlString) {
        ImageRequest imageRequest = new ImageRequest(urlString,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        ((AboutActivity)context).setBitmapImage(response);
                    }
                }, 0, 0, ImageView.ScaleType.CENTER_CROP, null,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Volley error (image)",
                                Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });

        queue.add(imageRequest);
    }

}