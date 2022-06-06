package org.xiong.xmock.engine;
import lombok.SneakyThrows;
import org.codehaus.jackson.type.JavaType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.xiong.xmock.api.base.TestCaseMetadata;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class ReflectionUtils {

    static Class<?>[] dummyTypes = new Class<?>[0];
    static Object[] dummyParameters = new Object[0];
    static Class<?>[] callableTypes = new Class<?>[]{String.class};
    static boolean isSet;

    @SneakyThrows
    public static Class<?> getClass(String s)  {
        try {
            //return Class.forName(s,true,Thread.currentThread().getContextClassLoader() ) ;
            return TestCaseMetadata.xmockClassLoader.loadClass(s);
            //return Class.forName(s,true,Thread.currentThread().getContextClassLoader() ) ;
            //return Class.forName(s);
        } catch (Throwable e) {
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
            Class<?> cls =  Class.forName(clsName,true,Thread.currentThread().getContextClassLoader() ) ;
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
            System.err.println("invokeMethod exception, e="+e.getMessage());
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
        JavaType javaType = XmockTypeFactory.defaultInstance().constructParametricType(cla, new JavaType[0]);
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

    public static JavaType getJavaType(Type type ) {

        if ( type instanceof ParameterizedType ) {
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            //获取泛型类型
            Class rowClass = (Class) ((ParameterizedType) type).getRawType();

            JavaType[] javaTypes = new JavaType[actualTypeArguments.length];

            for (int i = 0; i < actualTypeArguments.length; i++) {
                //泛型也可能带有泛型，递归获取
                javaTypes[i] = getJavaType( actualTypeArguments[i] );
            }
            return XmockTypeFactory.defaultInstance().constructParametricType(rowClass, javaTypes);
        } else {

            Class cla = (Class) type;
            return XmockTypeFactory.defaultInstance().constructParametricType(cla, new JavaType[0] );
            //return TypeFactory.defaultInstance().constructParametricType(cla, new JavaType[0] );
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


    static boolean allow( Object sourceInstance ){
        Annotation[] annotations = sourceInstance.getClass().getAnnotations();
        if( annotations != null ){
            for( Annotation anno : annotations ){
                if( anno.annotationType().getName().equals("org.springframework.stereotype.Service")
                        || anno.annotationType().getName().equals("org.springframework.stereotype.Component")
                        || anno.annotationType().getName().equals("org.springframework.web.bind.annotation.RestController")
                        || anno.annotationType().getName().equals("org.springframework.stereotype.Controller")
                        || anno.annotationType().getName().equals("org.xiong.xmock.engine.annotation.XMock")
                        || anno.annotationType().getName().equals("org.junit.runner.RunWith")
                ){
                    return true;
                }
            }
        }
        return false;
    }

    public static void inject (
            Class proxyType,Object sourceInstance
            ,Object proxyValue, Map<String,String> serviceName
            ,String ... fieldAnno ){

        injectByType( proxyType ,sourceInstance ,proxyValue ,serviceName,fieldAnno );

        if( serviceName.size() > 0 ) {
            injectByName( proxyType, sourceInstance, proxyValue, serviceName, fieldAnno);
        }
    }

    static void injectByName (
             Class proxyType,Object sourceInstance
            ,Object proxyValue, Map<String,String> serviceName
            ,String ... fieldAnno ) {

        if( sourceInstance == null || !allow( sourceInstance) ){
            return;
        }

        Field[] fields = sourceInstance.getClass().getDeclaredFields();
        for ( Field f : fields ) {

            String targetServiceName;
            String serviceAliasName = getTargetServiceAliasName(f);
            if ( isBlank( serviceAliasName ) ) {
                targetServiceName = serviceName.get(f.getName());
            } else {
                targetServiceName = serviceName.get(serviceAliasName);
            }

            if ( isBlank( targetServiceName )
                    || !targetServiceName.equals( proxyType.getName() )
                    || !assignment( f, proxyType, sourceInstance, proxyValue, fieldAnno )) {

                injectByName( proxyType ,getFieldValue(f, sourceInstance),proxyValue ,serviceName, fieldAnno );
            }
        }
    }

    static void injectByType(
            Class proxyType
            ,Object sourceInstance
            ,Object proxyValue,Map<String,String> serviceName
            ,String ... fieldAnno ){

        if( sourceInstance == null || !allow( sourceInstance) ){
            return;
        }

        Field[] fields = sourceInstance.getClass().getDeclaredFields();
        //by default type inject
        for ( Field f : fields ) {

            String targetServiceName = null ;
            String serviceAliasName = getTargetServiceAliasName(f);
            if( serviceName.size() > 0 ) {

                if (isBlank(serviceAliasName)) {
                    targetServiceName = serviceName.get(f.getName());
                } else {
                    targetServiceName = serviceName.get(serviceAliasName);
                }
            }
//            assignment(f, proxyType, sourceInstance, proxyValue , fieldAnno);
//            injectByType( proxyType ,getFieldValue(f, sourceInstance) ,proxyValue,serviceName, fieldAnno );

            if( !isBlank( serviceAliasName )
                    || !isBlank( targetServiceName )
                    || !assignment(f, proxyType, sourceInstance, proxyValue , fieldAnno)){
                injectByType( proxyType ,getFieldValue(f, sourceInstance) ,proxyValue,serviceName, fieldAnno );
            }
        }
    }

    public static void scanByAnnoAndInject(
            Class<? extends Annotation> typeAnno
            ,Class proxyType,Object sourceInstance
            ,Object proxyValue, Map<String,String> serviceName
            ,String ... fieldAnno ) {

        Class sourceType = sourceInstance.getClass();
        Annotation clzAnno = sourceType.getAnnotation(typeAnno);

        if ( clzAnno != null )
            instanceInjection( sourceInstance.getClass(), proxyType, sourceInstance ,proxyValue ,serviceName, fieldAnno);
    }

    static void instanceInjection(Class clz,Class proxyType,Object sourceInstance
            ,Object proxyValue, Map<String,String> serviceName,String ... fieldAnno){

        Field[] fields = clz.getDeclaredFields();

        //if user specify service's alias. so,by alias inject
        if( serviceName.size() > 0 ) {
            for (Field f : fields) {

                String targetServiceName;
                String serviceAliasName = getTargetServiceAliasName(f);
                if (isBlank(serviceAliasName)) {
                    targetServiceName = serviceName.get(f.getName());
                } else {
                    targetServiceName = serviceName.get(serviceAliasName);
                }

                if (!isBlank(targetServiceName) && targetServiceName.equals(proxyType.getName())) {
                    assignment(f, proxyType, sourceInstance, proxyValue, fieldAnno);
                    return;
                }
            }
        }

        //by default type inject
        for (Field f : fields) {
            assignment(f, proxyType, sourceInstance, proxyValue , fieldAnno);
        }
    }

    static String getTargetServiceAliasName( Field f ){

        Annotation [] annotations = f.getAnnotations();
        for( Annotation anno : annotations ){
            if( anno.annotationType().getName().equals("javax.annotation.Resource") ){
                Resource resource = (Resource)anno;
                return resource.name();
            }
            if( anno.annotationType().getName().equals("org.springframework.beans.factory.annotation.Qualifier") ){
                Qualifier qualifier = (Qualifier)anno;
                return qualifier.value();
            }
        }
        return null;
    }

    static boolean hasAnnotation( Field f, String ... fieldAnno ){

        Annotation [] annotations = f.getAnnotations();
        for( Annotation an : annotations ){
            for( String annoName : fieldAnno ){
                if(an.annotationType().getName().equals( annoName )){
                    return true;
                }
            }
        }
        return false;
    }

    static boolean assignment(Field f , Class type
            ,Object instance,Object value,String ... fieldAnno ){

        if(type.isInterface()
                && f.getType().isAssignableFrom(type)){
            try {
                f.setAccessible(true);
                f.set(instance,value);
                return true;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if( f.getType().isAssignableFrom(type)
                && hasAnnotation( f, fieldAnno )){
            try {
                init( getFieldValue(f, instance),value);
                f.setAccessible(true);
                f.set(instance,value);

                return true;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return false;
    }


    @SneakyThrows
    static void init(Object sourceBean, Object newBean ) {
        if(sourceBean == null){
            return;
        }

        Field[] newBeanFields = newBean.getClass().getDeclaredFields();
        Field[] sourceBeanFields = sourceBean.getClass().getDeclaredFields();
        if( sourceBeanFields != null && sourceBeanFields.length > 0 ){

            for ( Field f : sourceBeanFields ){
                Object fieldValue = getFieldValue(f , sourceBean );
                Field field = getField( f.getName(), newBeanFields );

                if (field == null || Modifier.isStatic(field.getModifiers())){
                    continue;
                }
                field.setAccessible(true);
                try {
                    field.set( newBean,fieldValue );
                } catch (IllegalAccessException e) {
                    System.out.println("copy sourceBean value error "+e.getMessage());
                    throw new RuntimeException(" copy sourceBean value error " );
                }
            }
        }
    }


    static Field getField( String fieldName, Field[] fields ){
        if( fields != null && fields.length > 0 ){
            for ( Field f : fields ){
                if( fieldName.equals( f.getName() )){
                    return f;
                }
            }
        }
        return null;
    }


    static Object getFieldValue( Field field, Object o ) {
        try {
            field.setAccessible(true);
            Object value = field.get( o );
            return value;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    static List getFiledsInfo(Object o , String ... fieldAnno) {
        Field[] fields = o.getClass().getDeclaredFields();
        List list = new ArrayList();
        Map infoMap = null;
        for (int i = 0; i < fields.length; i++) {
            if( hasAnnotation(fields[i], fieldAnno )){
                infoMap = new HashMap();
                infoMap.put("type", fields[i].getType());
                infoMap.put("name", fields[i].getName());
                infoMap.put("value", getFieldValue(fields[i], o));
                list.add(infoMap);
            }
        }
        return list;
    }
}
