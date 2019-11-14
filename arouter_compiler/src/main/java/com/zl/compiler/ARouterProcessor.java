package com.zl.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import com.zl.annotation.ARouter;
import com.zl.annotation.module.RouterBean;
import com.zl.compiler.utils.Constants;
import com.zl.compiler.utils.EmptyUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;


@AutoService(Processor.class)
//允许支持的注解类型，让注解处理器处理
@SupportedAnnotationTypes({Constants.AROUTER_ANNOTATION_TYPES})
//指定JDK编译版本
@SupportedSourceVersion(SourceVersion.RELEASE_7)
//接收build.gradle传过来的参数
@SupportedOptions({Constants.MODULE_NAME, Constants.APT_PACKAGE})
public class ARouterProcessor extends AbstractProcessor {


    private Elements elementsUtils;
    private Types typesUtils;
    private Messager messager;
    private Filer filer;

    //子模块名，如：app/order/personal，需要拼接类名时用到 ARouter$$Path$$Order
    private String moduleName;
    //包名，用于存放APT生成的类文件
    private String packageNameForAPT;

    //临时map存储，用来存放路由组Group对应的详细Path类对象，生成路由路径类文件时遍历
    //key:组名"app",value:"app"组的路由路径"ARouter$$Path$$app.class
    private Map<String, List<RouterBean>> tempPathMap = new HashMap<>();

    //临时map存储，用来存放路由组Group信息，生成路由组类文件时遍历
    //key:组名"app",value:"app"组的路由路径"ARouter$$Path$$app.class
    private Map<String, String> tempGroupMap = new HashMap<>();


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementsUtils = processingEnvironment.getElementUtils();
        typesUtils = processingEnvironment.getTypeUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();

        //通过ProcessingEnvironment去获取对应的参数
        Map<String, String> options = processingEnvironment.getOptions();
        if (!EmptyUtils.isEmpty(options)) {
            moduleName = options.get(Constants.MODULE_NAME);
            packageNameForAPT = options.get(Constants.APT_PACKAGE);
            messager.printMessage(Diagnostic.Kind.NOTE, "moduleName >>>" + moduleName);
            messager.printMessage(Diagnostic.Kind.NOTE, "packageName >>>" + packageNameForAPT);

        }

        //必传参数判空（乱码问题：添加java控制台输出中文乱码）
        if (EmptyUtils.isEmpty(moduleName) || EmptyUtils.isEmpty(packageNameForAPT)) {
            throw new RuntimeException("注解处理器需要的参数moduleName或者packageName为空，请在对应的build.gradle配置参数");
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        //一旦有类上使用@ARouter注解
        if (!EmptyUtils.isEmpty(set)) {
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(ARouter.class);

            if (!EmptyUtils.isEmpty(elements)) {
                //解析元素
                try {
                    parseElements(elements);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //注意！！！
            return true;
        }
        return false;
    }

    //解析所有被@ARouter注解的元素集合
    private void parseElements(Set<? extends Element> elements) throws IOException {

        //通过Element工具类，获取Activity类型
        TypeElement activityType = elementsUtils.getTypeElement(Constants.ACTIVITY);
        //显示类信息
        TypeMirror activityMirror = activityType.asType();


        for (Element element : elements) {
            //获取每个元素的信息类
            TypeMirror elementMirror = element.asType();
            messager.printMessage(Diagnostic.Kind.NOTE, "遍历的元素信息为：" + elementMirror.toString());
            //获取每个@ARouter上的注解，对应的path值
            ARouter aRouter = element.getAnnotation(ARouter.class);

            //路由详细信息，封装到实体类
            RouterBean bean = new RouterBean.Builder()
                    .setGroup(aRouter.group())
                    .setPath(aRouter.path())
                    .setElement(element)
                    .build();
            //高级判断，@ARouter注解仅仅只能用在类之上，并且是规定的Activity
            if (typesUtils.isSubtype(elementMirror, activityMirror)) {
                bean.setType(RouterBean.Type.ACTIVITY);
            } else {
                throw new RuntimeException("@ARouter注解目前仅限用于Activity之上");
            }

            //赋值临时map存储以上信息，用来遍历生成代码
            valueOfPathMap(bean);

        }
        TypeElement groupLoadType = elementsUtils.getTypeElement(Constants.AROUTER_GROUP);
        TypeElement pathLoadType = elementsUtils.getTypeElement(Constants.AROUTER_PATH);

        //1、生成路由的详细Path类文件，如：ARouter$$Path$$App
        createPathFile(pathLoadType);

        //2、生成路由组Group类文件（没有Path类文件，取不到）如：ARouter$$Group$$App
        createGroupFile(groupLoadType, pathLoadType);
    }

    /**
     * 生成路由组Group对应详细Path，如：ARouter$$Path$$app
     *
     * @param pathLoadType ARouterLoadPath接口信息
     */
    private void createPathFile(TypeElement pathLoadType) throws IOException {
        if (EmptyUtils.isEmpty(tempPathMap)) return;

        //方法的返回值Map<String,RouterBean>
        TypeName methodReturns = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouterBean.class));

        //遍历分组，每一个分组创建一个路径类文件，如：ARouter$$Path$$app
        for (Map.Entry<String, List<RouterBean>> entry : tempPathMap.entrySet()) {
            //方法体构造  public Map<String, RouterBean> loadPath() {
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constants.PATH_METHOD_NAME)//方法名
                    .addAnnotation(Override.class)//重写注解
                    .addModifiers(Modifier.PUBLIC)//public 修饰符
                    .returns(methodReturns);

            //不循环部分Map<String, RouterBean> pathMap = new HashMap<>();
            methodBuilder.addStatement("$T<$T, $T> $N = new $T<>()",
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(RouterBean.class),
                    Constants.PATH_PARAMETER_NAME,
                    HashMap.class);

            //  app/MainActivity, app/....
            List<RouterBean> pathList = entry.getValue();
            for (RouterBean bean : pathList) {
                //方法内容的循环部分
                /**
                 *  pathMap.put("/app/MainActivity",
                 *                     RouterBean.create(RouterBean.Type.ACTIVITY,
                 *                             MainActivity.class,
                 *                             "/app/MainActivity",
                 *                             "app"));
                 */
                methodBuilder.addStatement("$N.put($S,$T.create($T.$L,$T.class,$S,$S))",
                        Constants.PATH_PARAMETER_NAME,//pathMap.put
                        bean.getPath(),//"/app/MainActivity"
                        ClassName.get(RouterBean.class),
                        ClassName.get(RouterBean.Type.class),
                        bean.getType(),//枚举ACTIVITY
                        ClassName.get((TypeElement) bean.getElement()),//MainActivity.class
                        bean.getPath(),//"/app/MainActivity"
                        bean.getGroup());//"app"
            }

            //遍历过后，最后 return pathMap;
            methodBuilder.addStatement("return $N", Constants.PATH_PARAMETER_NAME);

            //生成类文件，如：ARouter$$Path$$app
            String finalClassName = Constants.PATH_FILE_NAME + entry.getKey();
            messager.printMessage(Diagnostic.Kind.NOTE, "APT生成路由Path类文件为："
                    + packageNameForAPT + "." + finalClassName);
            JavaFile.builder(packageNameForAPT,//包路径
                    TypeSpec.classBuilder(finalClassName)//类名
                            .addSuperinterface(ClassName.get(pathLoadType))//实现接口
                            .addModifiers(Modifier.PUBLIC)//
                            .addMethod(methodBuilder.build())//方法的构建
                            .build())
                    .build()
                    .writeTo(filer);
            //注意！！！组名赋值
            tempGroupMap.put(entry.getKey(),finalClassName);

        }
    }

    /**
     * 生成路由组Group文件，如：ARouter$$Path$$app
     *
     * @param groupLoadType ARouterLoadGroup接口信息
     * @param pathLoadType  ARouterLoadPath接口信息
     */
    private void createGroupFile(TypeElement groupLoadType, TypeElement pathLoadType) throws IOException {
        if(EmptyUtils.isEmpty(tempGroupMap)||EmptyUtils.isEmpty(tempPathMap))return;
        //方法的返回值Map<String, Class<? extends ARouterLoadPath>>
        TypeName methodReturns = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(ClassName.get(Class.class),
                WildcardTypeName.subtypeOf(ClassName.get(pathLoadType)))
        );

        //方法配置： public Map<String, Class<? extends ARouterLoadPath>> loadGroup() {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constants.GROUP_METHOD_NAME)//方法名
                .addAnnotation(Override.class)//重写注解
                .addModifiers(Modifier.PUBLIC)//public 修饰符
                .returns(methodReturns);//方法返回值

        //遍历之前Map<String, Class<? extends ARouterLoadPath>> groupMap = new HashMap<>();
         methodBuilder.addStatement("$T<$T,$T>$N = new $T<>()",
                 ClassName.get(Map.class),
                 ClassName.get(String.class),
                 ParameterizedTypeName.get(ClassName.get(Class.class),
                         WildcardTypeName.subtypeOf(ClassName.get(pathLoadType))),
                         Constants.GROUP_PARAMETER_NAME,
                         HashMap.class);

        // 方法内容配置
        for (Map.Entry<String, String> entry : tempGroupMap.entrySet()) {
            // 类似String.format("hello %s net163 %d", "net", 163)通配符
            // groupMap.put("main", ARouter$$Path$$app.class);
            methodBuilder.addStatement("$N.put($S, $T.class)",
                    Constants.GROUP_PARAMETER_NAME, // groupMap.put
                    entry.getKey(),
                    // 类文件在指定包名下
                    ClassName.get(packageNameForAPT, entry.getValue()));
        }

        // 遍历之后：return groupMap;
        methodBuilder.addStatement("return $N", Constants.GROUP_PARAMETER_NAME);

        // 最终生成的类文件名
        String finalClassName = Constants.GROUP_FILE_NAME + moduleName;
        messager.printMessage(Diagnostic.Kind.NOTE, "APT生成路由组Group类文件：" +
                packageNameForAPT + "." + finalClassName);

        // 生成类文件：ARouter$$Group$$app
        JavaFile.builder(packageNameForAPT, // 包名
                TypeSpec.classBuilder(finalClassName) // 类名
                        .addSuperinterface(ClassName.get(groupLoadType)) // 实现ARouterLoadGroup接口
                        .addModifiers(Modifier.PUBLIC) // public修饰符
                        .addMethod(methodBuilder.build()) // 方法的构建（方法参数 + 方法体）
                        .build()) // 类构建完成
                .build() // JavaFile构建完成
                .writeTo(filer); // 文件生成器开始生成类文件
    }

    /**
     * 赋值临时map存储，用来存放路由组Group对应的详细Path类对象，生成路由路径类文件时遍历
     *
     * @param bean 路有详细信息，最终实体封装类
     */
    private void valueOfPathMap(RouterBean bean) {

        if (checkRouterPath(bean)) {

            messager.printMessage(Diagnostic.Kind.NOTE, "RouterBean >>>" + bean.toString());
            //开始赋值
            List<RouterBean> routerBeans = tempPathMap.get(bean.getGroup());
            //如果从Map中找不到key
            if (EmptyUtils.isEmpty(routerBeans)) {
                routerBeans = new ArrayList<>();
                routerBeans.add(bean);
                tempPathMap.put(bean.getGroup(), routerBeans);
            } else {//找到key，直接加入临时集合
                routerBeans.add(bean);

            }
        } else {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解未按规范：如:/app/MainActivity");
        }
    }

    /**
     * 校验@ARouter注解值，如Group未填写就从必填项Path中截取数据
     *
     * @param bean 路有详细信息，最终实体封装类
     * @return
     */
    private boolean checkRouterPath(RouterBean bean) {
        String group = bean.getGroup();
        String path = bean.getPath();

        //@ARouter注解的path值，必须要以 / 为开头(模仿阿里的ARouter路由架构)
        if (EmptyUtils.isEmpty(path) || !path.startsWith("/")) {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解未按规范：如:/app/MainActivity");
            return false;
        }

        //比如开发者代码为：path = /MainActivity
        if (path.lastIndexOf("/") == 0) {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解未按规范：如:/app/MainActivity");
            return false;
        }

        ///从第一个"/"到第二个"/"中间截取出组名
        String finalGroup = path.substring(1, path.indexOf("/", 1));
        //比如开发者代码为：path = /MainActivity/MainActivity/MainActivity
        if (finalGroup.contains("/")) {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解未按规范：如:/app/MainActivity");
            return false;
        }

        //@ARouter注解中的group赋值
        if (!EmptyUtils.isEmpty(group) && !group.equals(moduleName)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解中的group值必须和当前子模块名相同！");
            return false;
        } else {
            bean.setGroup(finalGroup);
        }

        return true;
    }
}
