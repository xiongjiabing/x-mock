package org.xiong.xmock.core.agent;
import org.xiong.xmock.core.CoreMain;

public class AgentProcessor {
    private PremainArgWrapper premainArgWrapper;

    public AgentProcessor( PremainArgWrapper premainArgWrapper ){
        this.premainArgWrapper = premainArgWrapper;
    }

    public void premainProcessor(){
        try {
            CoreMain.preStart( this.premainArgWrapper );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AgentProcessor(){

    }

    public void premainProcessor(PremainArgWrapper premainArgWrapper){
        try {
            if(premainArgWrapper.isOpen()) {
                CoreMain.preStart(premainArgWrapper);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
