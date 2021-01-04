package com.msu.todolist.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.msu.todolist.R;
import com.msu.todolist.VolleyHelper;

public class AboutActivity extends AppCompatActivity {

    Button btnBack;
    ImageView imgView;
    VolleyHelper myVolley;

    private Matrix matrix = new Matrix();
    private float scale = 1f;
    private ScaleGestureDetector SGD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setTitle("ABOUT");

        imgView = findViewById(R.id.aboutAct_img2);

        myVolley = new VolleyHelper(this);
        myVolley.requestForBinaryData("http://www.ctis.bilkent.edu.tr/assets/images/ctislogo.png");

        SGD = new ScaleGestureDetector(this, new ScaleListener());

        btnBack = findViewById(R.id.aboutAct_btn1);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setBitmapImage(Bitmap response) {
        imgView.setImageBitmap(response);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        SGD.onTouchEvent(ev);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scale *= detector.getScaleFactor();
            scale = Math.max(0.1f, Math.min(scale, 5.0f));
            matrix.setScale(scale, scale);
            imgView.setImageMatrix(matrix);
            return true;
        }
    }
}