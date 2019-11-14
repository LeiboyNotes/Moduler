package com.zl.modular.order;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.zl.annotation.ARouter;
import com.zl.annotation.Parameter;
import com.zl.api.core.ParameterLoad;
import com.zl.common.base.BaseActivity;
import com.zl.common.utils.Cons;
@ARouter(path = "/order/Order_MainActivity")
public class Order_MainActivity extends BaseActivity {

    @Parameter
    String name;
    @Parameter(name="efs")
    int age = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity__main);
        Log.e(Cons.TAG, "common/Order_MainActivity");

        ParameterLoad parameterLoad = new Order_MainActivity$$Parameter();
        parameterLoad.loadParameter(this);
        if (getIntent() != null) {
            Log.e(Cons.TAG, "name >>>"+name+"/  age >>>"+age);
        }
    }

    public void jumpApp(View view) {
        //类加载的方式交互
//        try {
//            Class targetClass = Class.forName("com.zl.modular.MainActivity");
//            Intent intent = new Intent(this, targetClass);
//            intent.putExtra("name", "simon");
//            startActivity(intent);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//全局Map
//        Class<?> targetClass = RecordPathManager.getTargetClass("app", "MainActivity");
//        if (targetClass == null) {
//            Log.e(Cons.TAG, "获取targetClass为空");
//        }
//        Intent intent = new Intent(this, targetClass);
//        intent.putExtra("name", "simon");
//        startActivity(intent);
    }

    public void jumpPersonal(View view) {
    }

}
