package org.xiong.xmock.core;

import org.xiong.xmock.api.base.ContainerManger;
import org.xiong.xmock.core.agent.PremainArgWrapper;

public class XCombination {

    private final ISourceHandleChain iSourceHandleChain = new SourceHandleChain();
    private PremainArgWrapper premainArgWrapper;
    public XCombination( PremainArgWrapper premainArgWrapper){
        this.premainArgWrapper = premainArgWrapper;
    }

    public void execute() throws Exception {
        Class.forName(ContainerManger.class.getName(),true,Thread.currentThread().getContextClassLoader() ) ;
        //Class.forName("org.codehaus.jackson.map.type.TypeFactory",true,ClassLoader.getSystemClassLoader() );

        ContainerManger.SPI_INF.stream().forEach(sclass->{
            iSourceHandleChain.handle(new SourceAccept(sclass,premainArgWrapper) );
            iSourceHandleChain.reset();
        });
    }
}
