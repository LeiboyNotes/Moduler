package com.zl.modular.test;

import com.zl.api.core.ARouterLoadGroup;
import com.zl.api.core.ARouterLoadPath;

import java.util.HashMap;
import java.util.Map;

/**
 * 模拟ARouter路由器的组文件
 */
public class ARouter$$Group$$personal implements ARouterLoadGroup {
    @Override
    public Map<String, Class<? extends ARouterLoadPath>> loadGroup() {

        Map<String, Class<? extends ARouterLoadPath>> groupMap = new HashMap<>();
        groupMap.put("personal", ARouter$$Path$$personal.class);
        return groupMap;
    }
}
