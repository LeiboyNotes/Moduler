package com.zl.modular;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.zl.annotation.ARouter;
import com.zl.annotation.module.RouterBean;
import com.zl.api.ARouterLoadGroup;
import com.zl.api.ARouterLoadPath;
import com.zl.modular.test.ARouter$$Group$$order;
import com.zl.modular.test.ARouter$$Group$$personal;

import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;


@ARouter(path = "/app/MainActivity")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void jumpOrder(View view) {
//        Intent intent = new Intent(this, Order_MainActivity.class);
//        intent.putExtra("name", "simon");
//        startActivity(intent);

        ARouterLoadGroup loadGroup = new ARouter$$Group$$order();
        Map<String, Class<? extends ARouterLoadPath>> groupMap = loadGroup.loadGroup();
        //app----personal
        Class<? extends ARouterLoadPath> clazz = groupMap.get("order");

        try {
            ARouterLoadPath path = clazz.newInstance();
            Map<String, RouterBean> pathMap = path.loadPath();
            //获取/personal/Personal_MainActivity
            RouterBean routerBean = pathMap.get("/order/Order_MainActivity");
            if (routerBean != null) {
                Intent intent = new Intent(this, routerBean.getClazz());
                intent.putExtra("name", "simon");
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void jumpPersonal(View view) {

        //最终集成化模式，所有子模块APT生成的类文件都会打包到apk中
        ARouterLoadGroup loadGroup = new ARouter$$Group$$personal();
        Map<String, Class<? extends ARouterLoadPath>> groupMap = loadGroup.loadGroup();
        //app----personal
        Class<? extends ARouterLoadPath> clazz = groupMap.get("personal");

        try {
            ARouterLoadPath path = clazz.newInstance();
            Map<String, RouterBean> pathMap = path.loadPath();
            //获取/personal/Personal_MainActivity
            RouterBean routerBean = pathMap.get("/personal/Personal_MainActivity");
            if (routerBean != null) {
                Intent intent = new Intent(this, routerBean.getClazz());
                intent.putExtra("name", "simon");
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
