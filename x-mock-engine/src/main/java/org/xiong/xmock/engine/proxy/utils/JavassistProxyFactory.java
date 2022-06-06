package org.xiong.xmock.engine.proxy.utils;

import lombok.SneakyThrows;
import org.xiong.xmock.api.base.SchemaItem;
import org.xiong.xmock.api.base.SchemaItemManager;
import org.xiong.xmock.api.base.TestCaseMetadata;
import org.xiong.xmock.engine.EngineProcessor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class JavassistProxyFactory {

    @SneakyThrows
    public static  <T> T getProxy(Class<?> inf, Map<String, SchemaItem> mappings,InvocationHandler handler) {
        return (T) ProxyInterFaceGenerator.newProxyInstance(TestCaseMetadata.xmockClassLoader,
                inf,mappings, handler);
    }

    @SneakyThrows
    public static  <T> T getProxyClass(Class<?> inf, Map<String, SchemaItem> mappings, InvocationHandler handler) {
        return (T) ProxyClassGenerator.newProxyInstance(TestCaseMetadata.xmockClassLoader,
                inf,mappings, handler);
    }

    public static InvocationHandler getHandler( Map<String, SchemaItem> mappings ){
     return new InvocationHandler(){
         @Override
         public Object invoke( Object proxy, Method overridden, Object[] args) throws Throwable {

             final String signature = overridden.toString();
             AtomicReference<SchemaItem> schemaItemReference = new AtomicReference();
             mappings.forEach( (k,v) ->{
                 if( signature.contains(k) ){
                     schemaItemReference.set(v);
                     return;
                 }
             });
             return EngineProcessor.processorMethod( schemaItemReference.get(),overridden );
         }
     };
    }


    @SneakyThrows
    public Object doProxyMethod(String className, String methodName,String testClassName,String testCase){
        Method original = null;
        Class<?> cla = TestCaseMetadata.xmockClassLoader.loadClass(className);
        Method[] methods = cla.getDeclaredMethods();
        for (Method method: methods) {
            if(method.toString().contains(methodName)){
                original = method;
                break;
            }
        }
        Map<String,Map<String,SchemaItem>> mockClassMapping
                = SchemaItemManager.obtainActualSchemaItem( testClassName, testCase );
        if(mockClassMapping == null || mockClassMapping.get(className) == null){
            return null;
        }

        SchemaItem schemaItem = mockClassMapping.get(className).get(methodName);
        return EngineProcessor.processorMethod( schemaItem,original );
    }

}