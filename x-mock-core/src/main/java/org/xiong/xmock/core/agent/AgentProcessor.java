package org.xiong.xmock.core.agent;
import org.xiong.xmock.core.CoreMain;

public class AgentProcessor {
    private PremainArgWrapper premainArgWrapper;

    public AgentProcessor( PremainArgWrapper premainArgWrapper ){
        this.premainArgWrapper = premainArgWrapper;
    }

    public void premainProcessor(){
        CoreMain.preStart( premainArgWrapper );
    }
}
