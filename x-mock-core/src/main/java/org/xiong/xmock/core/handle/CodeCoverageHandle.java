package org.xiong.xmock.core.handle;
import org.xiong.xmock.api.CodeCoverage;
import org.xiong.xmock.api.base.ContainerManger;
import org.xiong.xmock.core.ISourceHandle;
import org.xiong.xmock.core.ISourceHandleChain;
import org.xiong.xmock.core.SourceAccept;
import org.xiong.xmock.core.XHandle;

@XHandle
public class CodeCoverageHandle implements ISourceHandle {

    @Override
    public void handle(SourceAccept accept, ISourceHandleChain iSourceHandleChain) {
        if ( CodeCoverage.class.isAssignableFrom( accept.getAClass())) {

            CodeCoverage codeCoverage = ContainerManger.loadContainerBySpi( CodeCoverage.class );
            if( codeCoverage != null ){
                ContainerManger.register( codeCoverage);
                codeCoverage.start();
            }
        }else{
            iSourceHandleChain.handle( accept );
        }
    }
}
