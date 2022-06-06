package org.xiong.xmock.engine;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.xiong.xmock.api.base.*;
import org.xiong.xmock.engine.proxy.utils.JavassistProxyFactory;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.apache.commons.lang3.StringUtils.*;

public class Xmock {

    public Xmock(){
    }


    public void doMockTest( Object source,String testClassName, String testScope ){
        TestCaseMetadata.agentInitComplete();
        try{
            if ( EngineProcessor.mockHasComplete( source )){
                return;
            }
            Map<String,String> serviceName = SchemaItemManager.getServiceName( testClassName );
            SchemaItemManager.obtainActualSchemaItem( testClassName, testScope )
                    .forEach(( targetMockClassName ,methodMapping )->{

                        Object mockInstance = null;
                        Class clazz = ReflectionUtils.getClass( targetMockClassName );
                        if( clazz.isInterface() ){
                            //if interface, by manual generate proxy object
                            mockInstance = JavassistProxyFactory.getProxy( clazz ,methodMapping, JavassistProxyFactory.getHandler( methodMapping ));
                            ReflectionUtils.inject( clazz,source,mockInstance,serviceName
                                    ,"org.xiong.xmock.engine.annotation.AutoInject"
                                    ,"javax.annotation.Resource"
                                    ,"org.springframework.beans.factory.annotation.Autowired" );
                        }
//                        else {
//                            try{
//                                mockInstance = clazz.newInstance();
//                                ReflectionUtils.inject( clazz,source,mockInstance,serviceName
//                                        ,"org.xiong.xmock.engine.annotation.AutoInject");
//                            }catch (Exception e){ e.printStackTrace();}
//                        }
                    });
        } catch ( Exception e ){
            throw e;
        } finally {
            EngineProcessor.mockComplete( source );
        }
    }


    // v1 版本 已作废
    @Deprecated
    public void doMockTest1( Object source,String testClassName, String testScope ){
        TestCaseMetadata.agentInitComplete();
        try{
            if ( EngineProcessor.mockHasComplete( source )){
                return;
            }
            Map<String,String> serviceName = SchemaItemManager.getServiceName( testClassName );
            SchemaItemManager.obtainActualSchemaItem( testClassName, testScope )
                    .forEach(( targetMockClassName ,methodMapping )->{

                        Object mockInstance = null;
                        Class clazz = ReflectionUtils.getClass( targetMockClassName );
                        if( clazz.isInterface() ){
                            //if interface, by manual generate proxy object
                            mockInstance = JavassistProxyFactory.getProxy( clazz ,methodMapping, JavassistProxyFactory.getHandler( methodMapping ));

                        } else {
                            // if ordinary Class,by high-level api generate proxy object
                           //mockInstance = mock( clazz );
                           mockInstance = JavassistProxyFactory.getProxyClass( clazz,methodMapping,JavassistProxyFactory.getHandler( methodMapping));

                            Service serviceAnno =  (Service)clazz.getDeclaredAnnotation( Service.class );
                            String service = null;
                            if( serviceAnno != null ){
                                service = serviceAnno.value();
                                if( isBlank(service) ){
                                    targetMockClassName = targetMockClassName.substring(targetMockClassName.lastIndexOf(".")+1);

                                    service = left(targetMockClassName, 1).toLowerCase()
                                            +right(targetMockClassName,targetMockClassName.length() - 1);
                                }
                            }

                            if ( !isBlank( service)
                                    && !serviceName.containsKey(service ) ){

                                serviceName.put( service, clazz.getName() );
                            }
                        }

                        //by x-mock framework inject
                        ReflectionUtils.inject( clazz,source,mockInstance,serviceName
                                ,"org.xiong.xmock.engine.annotation.AutoInject"
                                ,"javax.annotation.Resource"
                                ,"org.springframework.beans.factory.annotation.Autowired" );
                    });
        } catch ( Exception e ){
            throw e;
        } finally {
            EngineProcessor.mockComplete( source );
        }
    }


    @SneakyThrows
    static<T> T mock(Class<T> clazz ) {
        try {

            ProxyFactory factory = new ProxyFactory();
            factory.setSuperclass(clazz);

            Class aClass = factory.createClass();
            final T instance = (T) aClass.newInstance();

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Class " + clazz.getName() + " couldn't be mocked.",e);
        }
    }


    static class Binder<T> {
        private T mock;
        Map<String, SchemaItem> mappings;

        Binder(T mock, Map<String, SchemaItem> mappings ) {
            this.mock = mock;
            this.mappings = mappings;
        }

        public void bindReturnHandler() {
            MethodHandler handler = new MethodHandler() {
                @Override
                public Object invoke(Object self, Method overridden, Method proceed, Object[] args ) throws Throwable {
                    final String signature = overridden.toString();
                    AtomicReference<SchemaItem> schemaItemReference = new AtomicReference();
                    mappings.forEach( (k,v) ->{
                        if( signature.contains(k) ){
                            schemaItemReference.set(v);
                            return;
                        }
                    });
                    Object result = EngineProcessor.processorMethod(schemaItemReference.get(),overridden );
                    return result == null ? proceed.invoke( mock, args ) : result;
                }
            };
            ((ProxyObject) mock).setHandler(handler);
        }
    }


}
