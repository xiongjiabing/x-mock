package org.xiong.xmock.idl;
import javassist.CtClass;
import javassist.CtMethod;
import lombok.SneakyThrows;
import org.xiong.xmock.api.base.SchemaItem;
import org.xiong.xmock.api.base.SchemaItemManager;
import java.util.List;

public class ClientTransformer extends TransformerBase {
    private final YamlProcessor yamlProcessor = new YamlProcessor();

    @SneakyThrows
    @Override
    public byte[] internalTransform( String className ,String classFile, CtClass ctClass, CtMethod[] methods ,byte[] classfileBuffer ) {

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
        return classfileBuffer;
    }
}
