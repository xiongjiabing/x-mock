package org.xiong.xmock.engine;
import lombok.SneakyThrows;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.*;
import java.util.HashMap;
public class ReflectionUtils {

    static Logger log = LoggerFactory.getLogger(ReflectionUtils.class);

    static Class<?>[] dummyTypes = new Class<?>[0];
    static Object[] dummyParameters = new Object[0];
    static Class<?>[] callableTypes = new Class<?>[]{String.class};

    @SneakyThrows
    public static Class<?> getClass(String s)  {
        try {
            return Class.forName(s);
        } catch (Throwable e) {
            log.error("load mockClass:[{}] error",s,e);
            throw e;
        }
    }

    public static Object newObject( Class<?> cls ) {
        try {
            Constructor<?> cons = cls.getDeclaredConstructor();
            return cons.newInstance(new Object[]{});
        } catch (Exception e) {
            throw new RuntimeException("newObject exception", e);
        }
    }

    public static Object newObject( String clsName ) {
        try {
            Class<?> cls = Class.forName(clsName);
            Constructor<?> cons = cls.getDeclaredConstructor();
            return cons.newInstance(new Object[]{});
        } catch (Throwable e) {
            throw new RuntimeException("newObject exception", e);
        }
    }


    public static Object invokeMethod(Object obj, String methodName) {
        try {
            Method method = obj.getClass().getDeclaredMethod(methodName, dummyTypes);
            return method.invoke(obj, dummyParameters);
        } catch (Exception e) {
            log.error("invokeMethod exception, e=" + e.getMessage());
            return null;
        }
    }

    public static void checkInterface(Class<?> intf, Object obj) {
        if (intf.isAssignableFrom(obj.getClass())) return;
        throw new RuntimeException("not a valid service object");
    }

    public static void checkInterface(String intfName, Object obj) {
        try {
            Class<?> intf = Class.forName(intfName);
            if (intf.isAssignableFrom(obj.getClass())) return;
            throw new RuntimeException("not a valid service object");
        } catch (Throwable e) {
            throw new RuntimeException("interface not found, cls=" + intfName);
        }
    }


    public static Object getActualReturnValue( Type type ){

        if ( type instanceof ParameterizedType ){
            return null;
        }
        Class cla = (Class) type;
        JavaType javaType = TypeFactory.defaultInstance().constructParametricType(cla, new JavaType[0]);
        Class<?> res = javaType.getRawClass();

        if( res.isPrimitive() ){
            return 0;
        }

        if(  res.isAssignableFrom( Integer.class)
                || res.isAssignableFrom( Long.class)
                || res.isAssignableFrom( Double.class )
                || res.isAssignableFrom( Float.class )

        ){
            return 0;
        }

        if( res.isAssignableFrom( Boolean.class )){
            return true;
        }
        return null;
    }


    public static HashMap<String, Object> getMethodInfo(Class<?> intf) {
        try {

            Method[] methods = intf.getDeclaredMethods();
            HashMap<String, Object> methodNames = new HashMap<String, Object>();
            for (Method m : methods) {
                if (Modifier.isStatic(m.getModifiers())) continue;

                Class<?> [] reqCls = m.getParameterTypes();
                Type resType = m.getGenericReturnType();
                methodNames.put(m.getName() + "-req", reqCls );
                methodNames.put(m.getName() + "-res", resType );
            }
            return methodNames;
        } catch (Exception e) {
            throw new RuntimeException("getMethodInfo intf="+intf.getName(), e);
        }
    }

    public static boolean isWrap(Class cls){
        if( cls.isAssignableFrom( String.class )
                || cls.isAssignableFrom( Integer.class)
                || cls.isAssignableFrom( Long.class)
                || cls.isAssignableFrom( Double.class )
                || cls.isAssignableFrom( Float.class )
                || cls.isAssignableFrom( Boolean.class )
        ){
            return true;
        }
        return false;
    }

    public static Object getBaseTypeValue( Class<?> cl, Object value ) {
        String v = value.toString();
        if( cl.isAssignableFrom( String.class ))
            return v;
        if( Boolean.TYPE == cl || cl.isAssignableFrom( Boolean.class ) )
            return Boolean.parseBoolean( v );
        if( Double.TYPE == cl || cl.isAssignableFrom( Double.class ) )
            return Double.parseDouble(v);
        if( Float.TYPE == cl || cl.isAssignableFrom( Float.class ) )
            return Float.parseFloat( v );
        if( Integer.TYPE == cl || cl.isAssignableFrom( Integer.class) )
            return Integer.parseInt( v );
        if( Long.TYPE == cl || cl.isAssignableFrom( Long.class) )
            return Long.parseLong( v );

        return 0;
    }

    public static JavaType getJavaType(Type type) {

        if ( type instanceof ParameterizedType ) {
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            //获取泛型类型
            Class rowClass = (Class) ((ParameterizedType) type).getRawType();

            JavaType[] javaTypes = new JavaType[actualTypeArguments.length];

            for (int i = 0; i < actualTypeArguments.length; i++) {
                //泛型也可能带有泛型，递归获取
                javaTypes[i] = getJavaType(actualTypeArguments[i]);
            }
            return TypeFactory.defaultInstance().constructParametricType(rowClass, javaTypes);
        } else {

            Class cla = (Class) type;
            return TypeFactory.defaultInstance().constructParametricType(cla, new JavaType[0]);
        }
    }

    public static String getScanClassName( String testClassName ){
        String[] prefix = testClassName.split("\\.");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < prefix.length - 1; i++){
            if( i > 0 ){
                sb.append(".");
            }
            sb.append(prefix[i]);

            if( i >= 2 )
                break;
        }
        return sb.toString();
    }


}
