package org.xiong.xmock.idl;
import javassist.CtClass;
import javassist.CtMethod;
import lombok.SneakyThrows;

public class ServerTransformer extends TransformerBase {

    @SneakyThrows
    @Override
    public byte[] internalTransform( String classNam, String classFile, CtClass ctClass, CtMethod[] methods, byte[] classfileBuffer ) {
        if( ctClass.hasAnnotation("org.springframework.web.bind.annotation.RestController")
            || ctClass.hasAnnotation("org.springframework.stereotype.Controller")){
            for ( CtMethod m : methods ){
                if ( m.hasAnnotation("java.lang.Override")
                        || m.hasAnnotation("org.springframework.web.bind.annotation.RequestMapping")
                        || m.hasAnnotation("org.springframework.web.bind.annotation.PostMapping")
                        || m.hasAnnotation("org.springframework.web.bind.annotation.GetMapping")){
                    m.insertBefore("{ new org.xiong.xmock.engine.Xmock().doMock($0,\"appserver.mock\",null); }");
                    return ctClass.toBytecode();
                }
            }
        }
        return classfileBuffer;
    }
}
