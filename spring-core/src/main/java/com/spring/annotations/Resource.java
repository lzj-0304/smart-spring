package com.spring.annotations;

import java.lang.annotation.*;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Resource {
    String value() default "";
}
