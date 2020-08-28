package org.xiong.xmock.spring;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

public class XmockSpringContext {

    private static ApplicationContext context;
    private static Environment environment;
    public static final Map<String, Object> beans = new HashMap<>();

    public XmockSpringContext(){
    }

    public XmockSpringContext( ApplicationContext context, Environment environment){
        XmockSpringContext.context = context;
        XmockSpringContext.environment = environment;
    }

    private void init() {
        Map<String, Object> beanMapv0 = context.getBeansWithAnnotation( Service.class );
        Map<String, Object> beanMapv1 = context.getBeansWithAnnotation( Component.class );
        beans.putAll(beanMapv0);
        beans.putAll(beanMapv1);
    }

    private void close() {
    }

    public static ApplicationContext getApplicationContext(){
        return context;
    }

    public static Map<String, Object> getBeans(){
        return beans;
    }
}
