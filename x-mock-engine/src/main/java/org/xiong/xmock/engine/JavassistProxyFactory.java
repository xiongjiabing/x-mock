package org.xiong.xmock.engine;
import lombok.SneakyThrows;
import org.xiong.xmock.api.base.SchemaItem;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class JavassistProxyFactory {

    @SneakyThrows
    public static  <T> T getProxy(Class<?> inf, InvocationHandler handler) {
        return (T) ProxyGenerator.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                inf, handler);
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
             return EngineProcessor.processorMethodRes( schemaItemReference.get(),overridden );
         }
     };
    }
}