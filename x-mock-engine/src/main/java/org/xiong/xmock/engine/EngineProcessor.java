package org.xiong.xmock.engine;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.xiong.xmock.api.base.SchemaItem;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.*;
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
                String json = builtinFunction( mapper.writeValueAsString( schemaItem.getRes()) );
                JavaType javaType = ReflectionUtils.getJavaType( type  );

                result = mapper.readValue( json, javaType );
            }
        }
        return result;
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
