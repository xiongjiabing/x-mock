package org.xiong.xmock.engine;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.xiong.xmock.api.base.SchemaItem;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class EngineProcessor {

    public static Object processorMethodRes( SchemaItem schemaItem , Method method ) throws Throwable{
        Object result = null;
        ObjectMapper mapper = new ObjectMapper();
        if( schemaItem != null ){

            String throwError = schemaItem.getThrowError();
            if( "runtime".equals( throwError )){
                throw new RuntimeException();
            }

            String sleepStr = schemaItem.getSleep();
            if( !isBlank( sleepStr )){
                Thread.sleep(Long.parseLong( sleepStr ));
            }

            if( "timeout".equals( throwError )){
                throw new SocketTimeoutException();
            }

            if( schemaItem.getRes() != null ){
                result = schemaItem.getRes();
                Class returnClassType = method.getReturnType();

                if( returnClassType.getTypeName().equals("void") ){
                    return result;
                }
                if( returnClassType.isPrimitive() || ReflectionUtils.isWrap( returnClassType)){
                    return  ReflectionUtils.getBaseTypeValue( returnClassType ,result );
                }

                Type type = method.getGenericReturnType();
                String json = mapper.writeValueAsString( schemaItem.getRes() );
                JavaType javaType = ReflectionUtils.getJavaType( type );
                result = mapper.readValue( json, javaType );
            }
        }
        return result;
    }
}
