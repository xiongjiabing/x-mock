package org.xiong.xmock.idl;
import javassist.CtClass;
import javassist.CtMethod;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.xiong.xmock.api.base.SchemaItem;
import org.xiong.xmock.api.base.SchemaItemManager;

import java.lang.reflect.Modifier;
import java.util.List;

public class ServerTransformer extends TransformerBase {

    @SneakyThrows
    @Override
    public byte[] internalTransform( String classNam, String classFile, CtClass ctClass, CtMethod[] methods, byte[] classfileBuffer ) {
        if( ctClass.hasAnnotation("org.springframework.web.bind.annotation.RestController")
            || ctClass.hasAnnotation("com.xxl.job.core.handler.annotation.JobHandler")
            || ctClass.hasAnnotation("org.springframework.stereotype.Controller")){
            for ( CtMethod m : methods ){
                if ( !Modifier.isPrivate(m.getModifiers()) || (
                         m.hasAnnotation("org.springframework.web.bind.annotation.RequestMapping")
                        || m.hasAnnotation("org.springframework.web.bind.annotation.PostMapping")
                        || m.hasAnnotation("org.springframework.web.bind.annotation.GetMapping"))){
                    if(doneImplantSet.add(m.getLongName())) {
                        //m.insertBefore("{ new new org.xiong.xmock.api.boot.XmockFacade().doMock($0,\"appserver.mock\",null); }");
                        m.insertBefore("{ new org.xiong.xmock.api.boot.XmockFacade().doMock($0,\"appserver.mock\",null); }");
                    }
                    return ctClass.toBytecode();
                }
            }
        }

        List<SchemaItem> schemaItemList = SchemaItemManager.getTestClassMapping(null);
        if(schemaItemList != null && schemaItemList.size() > 0 && !ctClass.isInterface()){
            schemaItemList.forEach(r->{
                for ( CtMethod m : methods ){
                    //   (Modifier.isStatic(m.getModifiers()))
//                                || (Modifier.isPrivate(m.getModifiers()))
//                                || Modifier.isFinal(m.getModifiers())
                    if (StringUtils.isEmpty(r.getService())){
                        try{
                            if(m.getLongName().contains(r.getForName())){
                                if(doneImplantSet.add(m.getLongName())) {
                                    StringBuilder code = new StringBuilder("{");
                                    code.append(" Object res = org.xiong.xmock.api.boot.XmockFacade.invokeProxyMethod(\"" + r.getMockTargetClassSimpleName() + "\",\"" + r.getMockTargetMethodName() + "\");");
                                    code.append(" if(res != null){ return ($r)res;}");
                                    code.append("}");
                                    m.insertBefore(code.toString());
                                }
                            }
                        }catch (Exception e){ e.printStackTrace(); }
                    }
                }
            });
            return ctClass.toBytecode();
        }

        return classfileBuffer;
    }
}
