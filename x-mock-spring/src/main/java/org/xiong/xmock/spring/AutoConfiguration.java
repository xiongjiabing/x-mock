package org.xiong.xmock.spring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import java.util.Map;

@Configuration
public class AutoConfiguration implements ApplicationListener<ApplicationEvent> {

    static Logger log = LoggerFactory.getLogger(AutoConfiguration.class);

    @Bean
    static public BeanFactoryPostProcessor postProcessor() {
        return new BootPostProcessor();
    }

    private boolean checkBeanExisted(String beanName, ApplicationContext context) {
        try {
            Object bean = context.getBean(beanName);
            return bean != null;
        } catch (Throwable e) {
            return false;
        }
    }

    @Bean(initMethod = "init", destroyMethod = "close")
    @ConditionalOnMissingBean(XmockSpringContext.class)
    public XmockSpringContext initBean( Environment environment, ApplicationContext context) {
        return new XmockSpringContext( context,environment);
    }


    <T> Map<String, T> loadBeanByType(Class<T> cls, ApplicationContext context) {
        return  context.getBeansOfType(cls);
    }

    public void onApplicationEvent(ApplicationEvent event) {
// System.out.println("boot onApplicationEvent called, event = " + event);
        if (event instanceof ContextRefreshedEvent) {

        }
    }

    Object loadBean(String impl, String interfaceName, BeanFactory beanFactory) {
        if (interfaceName == null) return null;

        String beanName;
        if (impl != null && !impl.isEmpty()) {
            beanName = impl;
        } else {
            int p = interfaceName.lastIndexOf(".");
            if (p < 0) return null;
            String name = interfaceName.substring(p + 1);
            beanName = name.substring(0, 1).toLowerCase() + name.substring(1);
        }
        try {
            Object o = beanFactory.getBean(beanName);
            return o;
        } catch (Exception e1) {
            try {
                Object o = beanFactory.getBean(Class.forName(interfaceName));
                return o;
            } catch (Throwable e2) {
                return null;
            }
        }
    }

    static class BootPostProcessor implements BeanFactoryPostProcessor {

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory0) throws BeansException {

            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) beanFactory0;

            Environment environment = (Environment) beanFactory.getBean("environment");

            String s = environment.getProperty("krpc.referer.interfaceName");
        }
    }

}

