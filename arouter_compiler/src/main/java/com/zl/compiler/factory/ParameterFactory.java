package com.zl.compiler.factory;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.zl.annotation.Parameter;
import com.zl.compiler.utils.Constants;
import com.zl.compiler.utils.EmptyUtils;


import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

public class ParameterFactory {

    //MainActivity t = (MainActivity) target;
    private static final String CONTENT = "$T t = ($T) target";

    //方法体构建
    private MethodSpec.Builder methodBuilder;

    //Messager用来报告错误、警告和其他提示信息
    private Messager messager;

    //type(类信息)工具类，包含用于操作TypeMirror的工具方法
    private Types typeUtils;

    //类名 如：MainActivity
    private ClassName className;

    private Elements elementUtils;

    public ParameterFactory(Builder builder) {
        this.messager = builder.messager;
        this.className = builder.className;
        typeUtils = builder.typeUtils;
        elementUtils = builder.elementUtils;

        //通过方法参数体构建方法体， public void loadParameter(Object target) {
        methodBuilder = MethodSpec.methodBuilder(Constants.PARAMETER_METHOD_NAME)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(builder.parameterSpec);
    }

    /**
     * 添加方法体内容的第一行:  MainActivity t = (MainActivity) target;
     */
    public void addFirstStatement() {
        //方法内容 MainActivity t = (MainActivity) target;
        methodBuilder.addStatement(CONTENT, className, className);
    }

    public MethodSpec build() {
        return methodBuilder.build();
    }

    /**
     * 构建方法体内容 如： t.name = t.getIntent().getStringExtra("name");
     *
     * @param element
     */
    public void buildStatement(Element element) {
        //遍历注解的属性节点， 生成函数体
        TypeMirror typeMirror = element.asType();
        //获取TypeKind枚举类型的序列号
        int type = typeMirror.getKind().ordinal();
        //获取属性名
        String fieldName = element.getSimpleName().toString();
        //获取注解的值
        String annotationValue = element.getAnnotation(Parameter.class).name();
        //判断注解值为空的情况下的处理(注解中有name值就用注解值)
        annotationValue = EmptyUtils.isEmpty(annotationValue) ? fieldName : annotationValue;
        //最终拼接的前缀
        String finalValue = "t." + fieldName;
        //t.name = t.getIntent().
        String methodContent = finalValue + "= t.getIntent().";

        //TypeKind类型不包含String
        if (type == TypeKind.INT.ordinal()) {
            //t.age = t.getIntent().getIntExtra("age",t.age);
            methodContent += "getIntExtra($S," + finalValue + ")";
        }else if (type == TypeKind.BOOLEAN.ordinal()) {
            //t.age = t.getIntent().getBooleanExtra("age",t.age);
            methodContent += "getBooleanExtra($S," + finalValue + ")";
        }else {
            //t.name = t.getIntent().getStringExtra("name");
            if(typeMirror.toString().equalsIgnoreCase(Constants.STRING)){
                methodContent+="getStringExtra($S)";
            }
        }

        //健壮代码
        if (methodContent.endsWith(")")) {
            //添加最终拼接方法内容语句
            methodBuilder.addStatement(methodContent,annotationValue);
        }else {
            messager.printMessage(Diagnostic.Kind.ERROR,"目前暂支持String、int、boolean传值");
        }
    }

    public static class Builder {

        //方法体构建
        private MethodSpec.Builder methodBuilder;

        //Messager用来报告错误、警告和其他提示信息
        private Messager messager;

        //type(类信息)工具类，包含用于操作TypeMirror的工具方法
        private Types typeUtils;

        //类名 如：MainActivity
        private ClassName className;

        //方法体参数
        private ParameterSpec parameterSpec;

        private Elements elementUtils;

        public Builder(ParameterSpec parameterSpec) {
            this.parameterSpec = parameterSpec;
        }

        public Builder setMethodBuilder(MethodSpec.Builder methodBuilder) {
            this.methodBuilder = methodBuilder;
            return this;
        }

        public Builder setMessager(Messager messager) {
            this.messager = messager;
            return this;
        }

        public Builder setTypeUtils(Types typeUtils) {
            this.typeUtils = typeUtils;
            return this;
        }

        public Builder setClassName(ClassName className) {
            this.className = className;
            return this;
        }

        public ParameterFactory build() {
            if (parameterSpec == null) {
                throw new IllegalArgumentException("parameterSpec方法参数体为空");
            }
            if (className == null) {
                throw new IllegalArgumentException("className方法参数体为空");
            }
            if (messager == null) {
                throw new IllegalArgumentException("messager方法参数体为空");
            }
            return new ParameterFactory(this);
        }

        public Builder setElementUtils(Elements elementUtils) {
            this.elementUtils = elementUtils;
            return this;
        }
    }
}
