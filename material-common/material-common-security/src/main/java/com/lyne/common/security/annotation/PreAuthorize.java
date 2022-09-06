package com.lyne.common.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限注解
 *
 * <p>使用范例：<p>@PreAuthorize(hasPerm = "system:post:edit")
 *
 * @author lyne
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PreAuthorize {
    /**
     * 验证用户是否具备某权限
     */
    String hasPerm() default "";

    /**
     * 验证用户是否不具备某权限
     */
    String lacksPerm() default "";

    /**
     * 验证用户是否具有以下任意一个权限
     */
    String[] hasAnyPerm() default {};

    /**
     * 验证用户是否具有以下任意一个角色
     */
    String[] hasAnyRoles() default {};

    /**
     * 判断用户是否拥有某个角色
     */
    String hasRole() default "";

    /**
     * 验证用户是否不具备某角色，与 isRole逻辑相反
     */
    String lacksRole() default "";
}
