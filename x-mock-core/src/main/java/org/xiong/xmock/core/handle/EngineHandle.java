package org.xiong.xmock.core.handle;
import org.xiong.xmock.api.Engine;
import org.xiong.xmock.api.base.ContainerManger;
import org.xiong.xmock.core.ISourceHandle;
import org.xiong.xmock.core.ISourceHandleChain;
import org.xiong.xmock.core.SourceAccept;
import org.xiong.xmock.core.XHandle;

@XHandle
public class EngineHandle implements ISourceHandle {

    @Override
    public void handle(SourceAccept accept, ISourceHandleChain iSourceHandleChain) {
        if ( Engine.class.isAssignableFrom( accept.getAClass())) {

            Engine engine = ContainerManger.loadContainerBySpi( Engine.class);
            if( engine != null ){
                ContainerManger.register( engine );
                engine.start();
            }
        }else{
            iSourceHandleChain.handle( accept );
        }
    }
}
