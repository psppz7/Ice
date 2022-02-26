package com.nowcoder.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) //注解可以描述方法
@Retention(RetentionPolicy.RUNTIME) //注解有效时长
public @interface LoginRequired {

}
