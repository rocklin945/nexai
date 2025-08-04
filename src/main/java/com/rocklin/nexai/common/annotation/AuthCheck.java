package com.rocklin.nexai.common.annotation;

import com.rocklin.nexai.common.enums.UserRoleEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {

    /**
     * 权限控制角色
     * @return
     */
    UserRoleEnum enableRole() default UserRoleEnum.USER;
}
