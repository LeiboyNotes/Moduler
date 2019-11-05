package com.zl.modular.pesrsonal.debug;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.zl.common.base.BaseActivity;
import com.zl.common.utils.Cons;
import com.zl.modular.pesrsonal.R;

public class Personal_DebugActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal__debug);
        Log.e(Cons.TAG,"common/Personal_DebugActivity");
    }
}
