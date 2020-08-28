package org.xiong.xmock.core.agent;

import java.lang.instrument.Instrumentation;
public class Agent {

    public static void premain(String agentArgs, Instrumentation inst) {
        new AgentProcessor(new PremainArgWrapper( agentArgs,inst ))
                .premainProcessor();
    }
} 
