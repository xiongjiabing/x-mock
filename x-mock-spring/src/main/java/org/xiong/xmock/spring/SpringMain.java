package org.xiong.xmock.spring;
import org.xiong.xmock.api.IOCcontainer;
import java.util.Map;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class SpringMain implements IOCcontainer {

    @Override
    public void start() {

    }

    public boolean isExist() {
        return XmockSpringContext.getApplicationContext() != null;
    }

    @Override
    public void inject( Class type, Object mockInstance, Map<String, String> serviceName ) {
        if( !isExist() )
            return;

        if( type.isInterface() ){
            Map< String, Object> beansMap = XmockSpringContext.getApplicationContext().getBeansOfType(type );
            beansMap.forEach((bname,obj)->{
                if( !isBlank( serviceName.get( bname )) ){

                } else{

                }
            });

        }else {
            Map<String, Object> beans = XmockSpringContext.getBeans();



        }
    }
}
