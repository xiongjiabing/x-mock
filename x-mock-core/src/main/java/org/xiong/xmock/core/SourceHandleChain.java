package org.xiong.xmock.core;

import org.xiong.xmock.api.base.ClassScanner;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceConfigurationError;

public class SourceHandleChain implements ISourceHandleChain{
    private volatile int index = 0;

    private static final List<ISourceHandle> sourceHandleList = new ArrayList<>();

    static {
        List<Class> classList = ClassScanner.scanByAnno( XHandle.class,"org.xiong.xmock.core.handle" );
        if( classList != null ){

            for ( Class cls :classList ) {
                if( !ISourceHandle.class.isAssignableFrom(cls ))
                    fail(cls,cls.getName()+" is not ISourceHandle subclass ");

                try {
                    sourceHandleList.add((ISourceHandle)cls.newInstance());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void handle(SourceAccept accept) {
       if(sourceHandleList.size() > 0 ){
           if( index >= sourceHandleList.size()){
              return;
           }
           ISourceHandle sourceHandle = sourceHandleList.get( index++ );
           sourceHandle.handle( accept,this);
       }
    }

    @Override
    public void reset() {
        index = 0;
    }

    private static void fail(Class<?> service, String msg)
            throws ServiceConfigurationError
    {
        throw new ServiceConfigurationError(service.getName() + ": " + msg);
    }

}
