package com.zl.compiler.utils;

public class Constants {

    //注解处理器支持的注解类型
    public static final String AROUTER_ANNOTATION_TYPES = "com.zl.annotation.ARouter";
    //注解处理器支持的注解类型
    public static final String PARAMETER_ANNOTATION_TYPES = "com.zl.annotation.Parameter";
    //每个子模块的模块名
    public static final String MODULE_NAME = "moduleName";
    //用于存放APT生成的类文件
    public static final String APT_PACKAGE = "packageNameForAPT";

    //String 全类名
    public static final String STRING = "java.lang.String";

    //Activity 全类名
    public static final String ACTIVITY = "android.app.Activity";

    //包名前封装
    public static final String BASE_PACKAGE = "com.zl.api";
    //路由组Group加载接口
    public static final String AROUTER_GROUP = BASE_PACKAGE + ".core.ARouterLoadGroup";
    //路由组Group对应详细的Path加载接口
    public static final String AROUTER_PATH = BASE_PACKAGE + ".core.ARouterLoadPath";
    //获取参数加载接口
    public static final String PARAMETER_LOAD = BASE_PACKAGE + ".core.ParameterLoad";

    //路由组Group对应详细的Path, 方法名
    public static final String PATH_METHOD_NAME = "loadPath";
    //路由组Group对应详细的Path, 参数名
    public static final String PATH_PARAMETER_NAME = "pathMap";
    //路由组Group, 方法名
    public static final String GROUP_METHOD_NAME = "loadGroup";
    //路由组Group, 参数名
    public static final String GROUP_PARAMETER_NAME = "groupMap";
    //获取参数, 参数名
    public static final String PARAMETER_METHOD_NAME = "loadParameter";
    //获取参数方法名
    public static final String PARAMETER_NAME = "target";

    //APT生成的路由组Group对应详细的Path类文件名
    public static final String PATH_FILE_NAME = "ARouter$$Path$$";
    //APT生成的路由组Group对应详细的Path类文件名
    public static final String GROUP_FILE_NAME = "ARouter$$Group$$";
    //APT生成的获取参数类文件名
    public static final String PARAMETER_FILE_NAME = "$$Parameter";
}
