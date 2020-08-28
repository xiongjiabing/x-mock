package org.xiong.xmock.core.handle;
import org.xiong.xmock.api.IOCcontainer;
import org.xiong.xmock.api.base.ContainerManger;
import org.xiong.xmock.core.ISourceHandle;
import org.xiong.xmock.core.ISourceHandleChain;
import org.xiong.xmock.core.SourceAccept;
import org.xiong.xmock.core.XHandle;

@XHandle
public class IocContainerHandle implements ISourceHandle {

    @Override
    public void handle(SourceAccept accept, ISourceHandleChain iSourceHandleChain)  {
        if ( IOCcontainer.class.isAssignableFrom( accept.getAClass()) ) {

            IOCcontainer iOCcontainer = ContainerManger.loadContainerBySpi( IOCcontainer.class);
            if( iOCcontainer != null ){
                ContainerManger.register( iOCcontainer );
                iOCcontainer.start();
            }
        }else{
            iSourceHandleChain.handle( accept );
        }
    }
}
