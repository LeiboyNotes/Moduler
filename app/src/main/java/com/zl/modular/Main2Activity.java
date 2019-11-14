package com.zl.modular;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.zl.annotation.ARouter;
import com.zl.annotation.Parameter;

@ARouter(path = "/app/Main2Activity")
public class Main2Activity extends AppCompatActivity {


    @Parameter
    String username;
    @Parameter
    boolean success;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }
}
