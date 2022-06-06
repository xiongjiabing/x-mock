package org.xiong.xmock.core.agent;

import lombok.Data;
import org.xiong.xmock.api.base.ResourceLoader;
import java.lang.instrument.Instrumentation;
import java.util.Properties;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Data
public class PremainArgWrapper {

    public PremainArgWrapper( String agentArgs,Instrumentation inst ){
        this.agentArgs = agentArgs;
        this.inst = inst;
    }
    private String agentArgs;
    private Instrumentation inst;


    public boolean isServer(){
        try {
            if( !isBlank( agentArgs) ) {
                String server = agentArgs.split("=")[1];
                if ("server".equals(server)) {
                    return true;
                }
            }
            Properties config = ResourceLoader.getSystemProperties();
            Object obj;
            if ((obj = config.get("xmock.model")) != null ){
                if( "server".equals(obj.toString()) ){
                    return true;
                }
            }

        }catch (Exception e){
           throw e;
        }
        return false;
    }

    public boolean isOpen(){
        try {
            Properties config = ResourceLoader.getSystemProperties();
            Object obj;
            if ((obj = config.get("xmock.model")) != null ){
                if( "close".equals(obj.toString()) ){
                    return false;
                }
            }

        }catch (Exception e){
            throw e;
        }
        return true;
    }
}
