package org.xiong.xmock.idl;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import org.apache.commons.lang3.StringUtils;
import org.xiong.xmock.api.base.SchemaItem;
import org.xiong.xmock.api.base.SchemaItemManager;
import org.xiong.xmock.api.base.TestCaseMetadata;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TransformerBase implements ClassFileTransformer {

    boolean preCheck( String className ){
        //TestCaseMetadata.getAgentInitialized()
        if( className == null
                || className.contains("$")
                || className.contains("BadPaddingException")
                || className.contains("com/intellij/rt")
                || className.contains("com/sun/proxy")
                || className.contains("sun/reflect")){
            return true;
        }
        return false;
    }

    protected final Set<String> doneImplantSet = new HashSet<>();

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
            //e.printStackTrace();
        }
        return classfileBuffer;
    }

    public byte[] internalTransform( String classNam , String classFile ,CtClass ctClass , CtMethod[] methods , byte[] classfileBuffer){ return null; }

}
