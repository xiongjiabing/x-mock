package org.xiong.xmock.idl;
import javassist.*;
import org.xiong.xmock.api.base.SchemaItem;
import org.xiong.xmock.api.base.SchemaItemManager;
import org.xiong.xmock.api.base.TestCaseMetadata;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.List;

public class Transformer implements ClassFileTransformer {
    private final YamlProcessor yamlProcessor = new YamlProcessor();
    private ClassPool pool;

    public Transformer(){
        if (pool == null) {
            pool = new ClassPool(true);
            pool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
        }
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if( preCheck(className) ){
            return classfileBuffer;
        }
        try {

               String classFile = className.replace('/', '.');
               CtClass ctClass = pool.get(classFile);
               CtMethod[] methods = ctClass.getDeclaredMethods();
               if( className.endsWith("Test")){
                   String yamlFile = className+".mock";

                   for ( CtMethod m : methods ){
                       if ( m.hasAnnotation("org.junit.Test")){
                           m.insertBefore("{ new org.xiong.xmock.engine.Xmock().doMock($0,\""+classFile+"\",\""+m.getName()+"\"); }");
                       }
                   }
                   List<SchemaItem> list = yamlProcessor.loadYamlOnServer( yamlFile );
                   SchemaItemManager.addTestClassMapping( classFile ,list );
                   return ctClass.toBytecode();
               }
//                if( ctClass.hasAnnotation("org.xiong.xmock.engine.annotation.XMock")
//                        || ctClass.hasAnnotation("org.springframework.boot.autoconfigure.SpringBootApplication")){
//                    for ( CtMethod m : methods ){
//                        if ( m.getName().equals("main")){
//                            m.insertBefore("{ new org.xiong.xmock.engine.Xmock().doMock(new java.lang.Object(),\"appserver.mock\",null); }");
//                            return ctClass.toBytecode();
//                        }
//                    }
//                }

       }catch (Exception e ){
            System.out.println(className);
           e.printStackTrace();
       }
       return classfileBuffer;
    }

    boolean preCheck( String className ){
        if( TestCaseMetadata.ac.get()
                || className == null
                || className.contains("com/intellij/rt")
                || className.contains("com/sun/proxy")
                || className.contains("sun/reflect")){
            return true;
        }
        return false;
    }

}
