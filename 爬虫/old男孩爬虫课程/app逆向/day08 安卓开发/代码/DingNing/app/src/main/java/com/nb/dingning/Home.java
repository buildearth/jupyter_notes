package com.nb.dingning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        SharedPreferences sp = getSharedPreferences("dn_device_id", MODE_PRIVATE);
        String token = sp.getString("token", "");
        Log.e("HOME---->", token);
    }
}