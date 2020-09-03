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
    private boolean isServer;

    public Transformer(){
        if ( pool == null ) {
             pool = new ClassPool(true);
             pool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
        }
    }

    public Transformer( boolean isServer ){
        if ( pool == null ) {
             pool = new ClassPool(true);
             pool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
        }
        this.isServer = isServer;
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

               if( isServer ){

                   if( ctClass.hasAnnotation("org.springframework.web.bind.annotation.RestController")){
                       for ( CtMethod m : methods ){
                           if ( m.hasAnnotation("java.lang.Override")
                                   || m.hasAnnotation("org.springframework.web.bind.annotation.PostMapping")
                                   || m.hasAnnotation("org.springframework.web.bind.annotation.GetMapping")){
                               m.insertBefore("{ new org.xiong.xmock.engine.Xmock().doMock($0,\"appserver.mock\",null); }");
                               return ctClass.toBytecode();
                           }
                       }
                   }
               } else {

                   if( className.endsWith("Test")
                           || ctClass.hasAnnotation("org.junit.runner.RunWith") ){
                       for ( CtMethod m : methods ){
                           if ( m.hasAnnotation("org.junit.Test")){
                               m.insertBefore("{ new org.xiong.xmock.engine.Xmock().doMock($0,\""+classFile+"\",\""+m.getName()+"\"); }");
                           }
                       }
                       String yamlFile = className+".mock";
                       List<SchemaItem> list = yamlProcessor.loadYamlOnServer( yamlFile );
                       SchemaItemManager.addTestClassMapping( classFile ,list );
                       return ctClass.toBytecode();
                   }
               }
       }catch (Exception e ){
            System.out.println(className);
           e.printStackTrace();
       }
       return classfileBuffer;
    }

    boolean preCheck( String className ){
        if( TestCaseMetadata.getAgentInitialized()
                || className == null
                || className.contains("$")
                || className.contains("BadPaddingException")
                || className.contains("com/intellij/rt")
                || className.contains("com/sun/proxy")
                || className.contains("sun/reflect")){
            return true;
        }
        return false;
    }

}
