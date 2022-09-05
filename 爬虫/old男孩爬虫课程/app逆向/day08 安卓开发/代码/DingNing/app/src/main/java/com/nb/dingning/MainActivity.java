package com.nb.dingning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    private TextView txtUser, txtPwd;
    private Button btnLogin, btnReset;
    public Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        initView();
        initListener();
    }

    private void initView() {
        txtUser = findViewById(R.id.txt_user);
        txtPwd = findViewById(R.id.txt_pwd);

        btnLogin = findViewById(R.id.btn_login);
        btnReset = findViewById(R.id.btn_reset);
    }

    private void initListener() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginForm();
            }
        });


        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("日志", "重置");
                txtUser.setText("");
                txtPwd.setText("");
            }
        });
    }

    /**
     * 点击登录
     */
    private void LoginForm() {
        Log.e("日志", "登录");
        // 1.获取用户名和密码
        /*
        String username = String.valueOf(txtUser.getText());
        if (username.length() == 0) {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        String password = String.valueOf(txtPwd.getText());
        if (password.length() == 0) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        */

        // {username:"123123",password:"999"}
        TreeMap<String, String> dataMap = new TreeMap<String, String>();

        HashMap<String, TextView> objMap = new HashMap<String, TextView>();
        objMap.put("username", txtUser);
        objMap.put("password", txtPwd);
        for (Map.Entry<String, TextView> entry : objMap.entrySet()) {
            String key = entry.getKey();
            TextView obj = entry.getValue();
            String value = String.valueOf(obj.getText());
            dataMap.put(key, value);
        }

        //passwordqwe123usernameroot
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key);
            sb.append(value);
        }
        String dataString = sb.toString();
        String signString = md5(dataString);
        dataMap.put("sign", signString);

        // dataMap = {username:"123123",password:"999",sign:"xfd;aksdjf;lkajsd;fkja;skdf;aksdf"}
        new Thread() {
            @Override
            public void run() {
                // 2.发送网络请求 - okhttp
                // 第一步：implementation "com.squareup.okhttp3:okhttp:4.9.1"  + 同步
                // 第二步：<uses-permission android:name="android.permission.INTERNET" />
                // 第三步：http请求（仅测试）
                OkHttpClient client = new OkHttpClient.Builder().build();
                FormBody form = new FormBody.Builder()
                        .add("user", dataMap.get("username"))
                        .add("pwd", dataMap.get("password"))
                        .add("sign", dataMap.get("sign")).build();
                Request req = new Request.Builder().url("http://192.168.43.252:5000/auth").post(form).build();
                Call call = client.newCall(req);
                try {
                    Response res = call.execute();
                    ResponseBody body = res.body();

                    // {"status":true,"token":"b96efd24-e323-4efd-8813-659570619cde"}
                    String bodyString = body.string();

                    // 1.获取登录状态 + token
                    HttpResponse obj = new Gson().fromJson(bodyString, HttpResponse.class);
                    // obj.token

                    // 2.token保存手机 -> 本地XML文件（登录凭证保存到cookie）
                    // /data/data/com.nb.dingning
                    SharedPreferences sp = getSharedPreferences("dn_device_id", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("token", obj.token);
                    editor.commit();

                    // 3.跳转
                    Intent in = new Intent(mContext, Home.class);
                    startActivity(in);


                    Log.e("获取相应的neriong -->", bodyString); // passwordqwe123usernameroot
                } catch (IOException ex) {
                    Log.e("请求异常 -->", "网络错误");
                }

            }
        }.start();

        // 3.跳转到其他的页面
    }

    /**
     * md5加密
     *
     * @param dataString 待加密的字符串
     * @return 加密结果
     */
    private String md5(String dataString) {
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            byte[] nameBytes = instance.digest(dataString.getBytes());

            // 十六进制展示
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < nameBytes.length; i++) {
                int val = nameBytes[i] & 255;  // 负数转换为正数
                if (val < 16) {
                    sb.append("0");
                }
                sb.append(Integer.toHexString(val));
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }

    }
}