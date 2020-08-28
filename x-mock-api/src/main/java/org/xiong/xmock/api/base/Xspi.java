package org.xiong.xmock.api.base;

import java.lang.annotation.*;

@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Xspi {

    int value() default -1;
    String name();
}
