package org.xiong.xmock.idl;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import org.xiong.xmock.api.base.TestCaseMetadata;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class TransformerBase implements ClassFileTransformer {

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

    private ClassPool pool;
    public TransformerBase(){
        if ( pool == null ) {
            pool = new ClassPool(true);
            pool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
        }
    }


    @Override
    public byte[] transform( ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if( preCheck( className )){
            return classfileBuffer;
        }
        try{
            String classFile = className.replace('/', '.');
            CtClass ctClass = pool.get(classFile);
            CtMethod[] methods = ctClass.getDeclaredMethods();

            return internalTransform( className , classFile , ctClass , methods ,classfileBuffer);
        }catch ( Exception e ){
        }
        return classfileBuffer;
    }

    public byte[] internalTransform( String classNam , String classFile ,CtClass ctClass , CtMethod[] methods , byte[] classfileBuffer){ return null; }
}
