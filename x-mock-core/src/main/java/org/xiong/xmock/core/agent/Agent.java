package org.xiong.xmock.core.agent;

import org.xiong.xmock.api.base.TestCaseMetadata;
import org.xiong.xmock.classloader.Bootstrap;
import org.xiong.xmock.classloader.XMockURLClassLoader;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;

public class Agent {

    public static void premain(String agentArgs, Instrumentation inst) throws Exception{
        ClassLoader root = Thread.currentThread().getContextClassLoader();
        XMockURLClassLoader xmockClassLoader = (XMockURLClassLoader) Bootstrap.createXmockClassLoader(root == null ? ClassLoader.getSystemClassLoader() : root);
        Thread.currentThread().setContextClassLoader(xmockClassLoader);
        TestCaseMetadata.xmockClassLoader = xmockClassLoader;
        Class startupClass = xmockClassLoader.loadClass("org.xiong.xmock.core.agent.AgentProcessor");

//        TestCaseMetadata.xmockClassLoader = ClassLoader.getSystemClassLoader();
//        Class startupClass =  ClassLoader.getSystemClassLoader().loadClass("org.xiong.xmock.core.agent.AgentProcessor");
        Object startupInstance = startupClass.getConstructor().newInstance();
        String methodName = "premainProcessor";
        Class<?> paramTypes[] = new Class[1];
        paramTypes[0] = Class.forName("org.xiong.xmock.core.agent.PremainArgWrapper");
        Object paramValues[] = new Object[1];
        paramValues[0] = new PremainArgWrapper( agentArgs,inst );

        Method method =
                startupInstance.getClass().getMethod(methodName, paramTypes);
        method.invoke(startupInstance, paramValues);

        /*
         * The logic of the old
         */
//            new AgentProcessor(new PremainArgWrapper( agentArgs,inst ))
//                    .premainProcessor();

        Thread.currentThread().setContextClassLoader(root);
    }
} 
