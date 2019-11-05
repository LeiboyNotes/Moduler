package com.zl.modular.order.debug;

import android.os.Bundle;
import android.util.Log;

import com.zl.common.base.BaseActivity;
import com.zl.common.utils.Cons;
import com.zl.modular.order.R;

public class Order_DebugActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order__debug);
        Log.e(Cons.TAG, "common/Order_DebugActivity");
    }
}
