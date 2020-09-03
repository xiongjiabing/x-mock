package org.xiong.xmock.core.handle;
import org.xiong.xmock.api.Describable;
import org.xiong.xmock.api.base.ContainerManger;
import org.xiong.xmock.core.ISourceHandle;
import org.xiong.xmock.core.ISourceHandleChain;
import org.xiong.xmock.core.SourceAccept;
import org.xiong.xmock.core.XHandle;
import org.xiong.xmock.core.agent.PremainArgWrapper;

@XHandle
public class DescribableHandle implements ISourceHandle {

    @Override
    public void handle( SourceAccept accept, ISourceHandleChain iSourceHandleChain ) {
        if ( Describable.class.isAssignableFrom( accept.getAClass())) {

            Describable describable = ContainerManger.loadContainerBySpi( Describable.class);
            if( describable != null ){

                ContainerManger.register( describable );
                PremainArgWrapper premainArgWrapper = (PremainArgWrapper)accept.getAddtions();
                if( premainArgWrapper.isServer() ) {

                    describable.start( premainArgWrapper.getInst(),"appserver.mock" );
                }else{
                    describable.start( premainArgWrapper.getInst() );
                }
            }
        }else{
            iSourceHandleChain.handle( accept );
        }
    }
}
