package com.zl.common.base;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.zl.common.utils.Cons;

public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(Cons.TAG,"common/BaseActivity");
    }
}
