package com.zl.modular.test;

import com.zl.api.ARouterLoadGroup;
import com.zl.api.ARouterLoadPath;

import java.util.HashMap;
import java.util.Map;

/**
 * 模拟ARouter路由器的组文件
 */
public class ARouter$$Group$$app implements ARouterLoadGroup {
    @Override
    public Map<String, Class<? extends ARouterLoadPath>> loadGroup() {

        Map<String, Class<? extends ARouterLoadPath>> groupMap = new HashMap<>();
        groupMap.put("app", ARouter$$Path$$app.class);
        return groupMap;
    }
}
