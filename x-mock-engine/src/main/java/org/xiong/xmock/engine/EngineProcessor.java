package org.xiong.xmock.engine;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.xiong.xmock.api.base.LocalLock;
import org.xiong.xmock.api.base.SchemaItem;
import org.xiong.xmock.api.base.TestCaseMetadata;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import static org.apache.commons.lang3.StringUtils.isBlank;
import java.text.SimpleDateFormat;
import java.util.*;

public class EngineProcessor {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static Set<String > testClassCache = new HashSet<>();

    public static boolean mockHasComplete( Object obj ){
        if( testClassCache.contains( obj.getClass().getName() ) ){
            return true;
        }
        LocalLock.lock(obj.getClass().getName());

        if( testClassCache.contains( obj.getClass().getName() ) ){
            return true;
        }
        return false;
    }

    public static void mockComplete( Object obj ){

        testClassCache.add( obj.getClass().getName() );
        LocalLock.unlock( obj.getClass().getName() );

    }

    public static Object processorMethod( SchemaItem schemaItem , Method method ) throws Throwable{
        Object result = null;

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

                if( !returnClassType.getName().equals(Object.class.getName())
                        && (returnClassType.isPrimitive() || ReflectionUtils.isWrap( returnClassType))){
                    return  ReflectionUtils.getBaseTypeValue( returnClassType ,result );
                }
                Type type = method.getGenericReturnType();
                if(!StringUtils.isBlank(schemaItem.getType())){
                    type = ReflectionUtils.getClass(schemaItem.getType());
                }

                JavaType javaType = invokeJavaType( type  );
                String json = builtinFunction( mapper.writeValueAsString( schemaItem.getRes()) );
                //JavaType javaType = ReflectionUtils.getJavaType( type  );
                result = mapper.readValue( json, javaType );
            }
        }
        return result;
    }



    @SneakyThrows
    public static JavaType invokeJavaType(Type type){
        Class startupClass = TestCaseMetadata.xmockClassLoader.loadClass("org.xiong.xmock.engine.ReflectionUtils");
        Method method = startupClass.getMethod("getJavaType",Type.class);
        JavaType javaType = (JavaType)method.invoke(null,type);
        return javaType;
    }


    private static Map<String ,FunctionProcessor> expressions = new HashMap<>();
    static String ss;

    static {
        ss = "\\";
        expressions.put("$time", new TimeProcessor());
        expressions.put("$date", new DateProcessor());
        expressions.put("$ts", new TsProcessor());
        expressions.put("$uuid", new UuidProcessor());
    }

    static String builtinFunction( String val ){
        for (Map.Entry<String, FunctionProcessor> m : expressions.entrySet()){

            if( StringUtils.countMatches(val,m.getKey()) > 0) {
                val = val.replaceAll( ss+m.getKey(), m.getValue().getVal());
            }
        }
        return val;
    }

    static class UuidProcessor extends FunctionProcessor {
        @Override
        public String getVal() {
            return UUID.randomUUID().toString();
        }
    }

    static class TsProcessor extends FunctionProcessor {
        @Override
        public String getVal() {
            return new Date().getTime()+"";
        }
    }

    static class TimeProcessor extends FunctionProcessor {
         ThreadLocal<SimpleDateFormat> f = ThreadLocal.withInitial(()->new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        @Override
        public String getVal() {
            String val = f.get().format( new Date() );
            f.remove();
            return val;
        }
    }

    static class DateProcessor extends FunctionProcessor {
        ThreadLocal<SimpleDateFormat> f = ThreadLocal.withInitial(()->new SimpleDateFormat("yyyy-MM-dd"));
        @Override
        public String getVal() {
            String val = f.get().format( new Date() );
            f.remove();
            return val;
        }
    }

    static class FunctionProcessor{
        public String getVal(){return null;};
    }
}
