package org.xiong.xmock.engine;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import org.xiong.xmock.engine.annotation.XMock;
import org.xiong.xmock.api.IOCcontainer;
import org.xiong.xmock.api.base.*;
import java.lang.reflect.Method;
import lombok.SneakyThrows;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Xmock {

    public void doMock( Object source,String testClassName, String testScope ){
        TestCaseMetadata.ac.compareAndSet(false,true );

        Map<String,String> serviceName = SchemaItemManager.getServiceName( testClassName );
        SchemaItemManager.obtainActualSchemaItem(testClassName, testScope)
                .forEach((targetMockClassName ,methodMappin )->{

                    Object mockInstance;
                    Class clazz = ReflectionUtils.getClass(targetMockClassName);
                    if( clazz.isInterface() ){
                        //if interface, by manual generate proxy object
                        mockInstance = JavassistProxyFactory.getProxy( clazz , JavassistProxyFactory.getHandler( methodMappin ));

                    } else {
                        // if ordinary Class,by high-level api generate proxy object
                        mockInstance = mock( clazz );
                        new Binder( mockInstance,methodMappin).bindReturnHandler();

                    }
                    //by x-mock framework inject
                    ClassScanner.scanByAnnoAndInject( XMock.class,clazz,source,mockInstance,serviceName
                            ,"org.xiong.xmock.engine.annotation.AutoInject"
                            ,"javax.annotation.Resource"
                            ,"org.springframework.beans.factory.annotation.Autowired" );

                    //if exist ioc container,by container inject
                    IOCcontainer iOCcontainer = ContainerManger.getContainer( IOCcontainer.class );
                    if( iOCcontainer != null ) {
                        iOCcontainer.inject( clazz, mockInstance, serviceName );
                    }
        });
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
                public Object invoke( Object self, Method overridden, Method proceed, Object[] args ) throws Throwable {
                    final String signature = overridden.toString();
                    AtomicReference<SchemaItem> schemaItemReference = new AtomicReference();
                    mappings.forEach( (k,v) ->{
                        if( signature.contains(k) ){
                            schemaItemReference.set(v);
                            return;
                        }
                    });
                    Object result = EngineProcessor.processorMethodRes(schemaItemReference.get(),overridden );
                    return result == null ? proceed.invoke( mock, args ) : result;
                }
            };
            ((ProxyObject) mock).setHandler(handler);
        }
    }

}
