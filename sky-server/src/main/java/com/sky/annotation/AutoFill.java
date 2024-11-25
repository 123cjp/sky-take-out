package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于标志方法需要进行功能的字段填充
 * @author 陈建平
 */
@Target(ElementType.METHOD)//指定注解只可以加在方法上面
@Retention(RetentionPolicy.RUNTIME)//元注解，表示直接的生命周期--需要在运行时动态获取注解信息
public @interface AutoFill {
    //数据库的操作类型
    OperationType value();
}
