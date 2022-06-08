package org.xiong.xmock.api.boot;

import lombok.SneakyThrows;
import org.xiong.xmock.api.base.TestCaseMetadata;
import java.lang.reflect.Method;

public class XmockFacade {

    @SneakyThrows
    public void doMock(Object source, String testClassName, String testScope){
        TestCaseMetadata testCaseMetadata = new TestCaseMetadata();
        testCaseMetadata.setTestClassName(testClassName);
        testCaseMetadata.setTestCase(testScope);
        TestCaseMetadata.LOCAL_TEST_CASE.set(testCaseMetadata);

        Class startupClass = TestCaseMetadata.xmockClassLoader.loadClass("org.xiong.xmock.engine.Xmock");
        //Class startupClass = ClassLoader.getSystemClassLoader().loadClass("org.xiong.xmock.engine.Xmock");
        Object startupInstance = startupClass.getConstructor().newInstance();

        Class<?> paramTypes[] = new Class[3];
        paramTypes[0] = Object.class;
        paramTypes[1] = String.class;
        paramTypes[2] = String.class;

        Object [] paramValues  = new Object[3];
        paramValues[0] = source;
        paramValues[1] = testClassName;
        paramValues[2] = testScope;

        Method method =
                startupInstance.getClass().getMethod("doMockTest", paramTypes);
        method.invoke(startupInstance, paramValues);
    }


    @SneakyThrows
    public static Object invokeProxyMethod(String className, String targetMethodName){
        TestCaseMetadata testCaseMetadata = TestCaseMetadata.LOCAL_TEST_CASE.get();
        if(testCaseMetadata == null){
            return null;
        }

        Class startupClass = TestCaseMetadata.xmockClassLoader.loadClass("org.xiong.xmock.engine.proxy.utils.JavassistProxyFactory");
        //Class startupClass = ClassLoader.getSystemClassLoader().loadClass("org.xiong.xmock.engine.proxy.utils.JavassistProxyFactory");
        Object startupInstance = startupClass.getConstructor().newInstance();

        Class<?> paramTypes[] = new Class[4];
        paramTypes[0] = String.class;
        paramTypes[1] = String.class;
        paramTypes[2] = String.class;
        paramTypes[3] = String.class;

        Object [] paramValues  = new Object[4];
        paramValues[0] = className;
        paramValues[1] = targetMethodName;
        paramValues[2] = testCaseMetadata.getTestClassName();
        paramValues[3] = testCaseMetadata.getTestCase();
        Method method =
                startupInstance.getClass().getMethod("doProxyMethod", paramTypes);
        return method.invoke(startupInstance, paramValues);
    }

}
