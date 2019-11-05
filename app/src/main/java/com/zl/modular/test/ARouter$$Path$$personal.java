package com.zl.modular.test;

import com.zl.annotation.module.RouterBean;
import com.zl.api.ARouterLoadPath;
import com.zl.modular.pesrsonal.Personal_MainActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * 模拟ARouter路由器的组文件，对应路径
 */
public class ARouter$$Path$$personal implements ARouterLoadPath {
    @Override
    public Map<String, RouterBean> loadPath() {

        Map<String, RouterBean> pathMap = new HashMap<>();
        pathMap.put("/personal/Personal_MainActivity",
                RouterBean.create(RouterBean.Type.ACTIVITY,
                        Personal_MainActivity.class,
                        "/personal/Personal_MainActivity",
                        "personal"));
        return pathMap;
    }
}
