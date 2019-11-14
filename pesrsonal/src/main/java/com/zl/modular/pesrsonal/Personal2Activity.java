package com.zl.modular.pesrsonal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.zl.annotation.ARouter;

@ARouter(path = "/personal/Personal2Activity")
public class Personal2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal2);
    }
}
