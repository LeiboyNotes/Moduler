package com.zl.modular;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.zl.annotation.ARouter;
import com.zl.annotation.Parameter;

@ARouter(path = "/app/Main3Activity")
public class Main3Activity extends AppCompatActivity {

    @Parameter
    String password;
    @Parameter
    int gender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
    }
}
