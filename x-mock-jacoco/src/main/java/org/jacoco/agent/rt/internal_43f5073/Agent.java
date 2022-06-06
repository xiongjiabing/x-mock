//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jacoco.agent.rt.internal_43f5073;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.Callable;
import org.jacoco.agent.rt.IAgent;
import org.jacoco.agent.rt.internal_43f5073.core.JaCoCo;
import org.jacoco.agent.rt.internal_43f5073.core.data.ExecutionDataWriter;
import org.jacoco.agent.rt.internal_43f5073.core.runtime.AbstractRuntime;
import org.jacoco.agent.rt.internal_43f5073.core.runtime.AgentOptions;
import org.jacoco.agent.rt.internal_43f5073.core.runtime.RuntimeData;
import org.jacoco.agent.rt.internal_43f5073.core.runtime.AgentOptions.OutputMode;
import org.jacoco.agent.rt.internal_43f5073.output.FileOutput;
import org.jacoco.agent.rt.internal_43f5073.output.IAgentOutput;
import org.jacoco.agent.rt.internal_43f5073.output.NoneOutput;
import org.jacoco.agent.rt.internal_43f5073.output.TcpClientOutput;
import org.jacoco.agent.rt.internal_43f5073.output.TcpServerOutput;
import org.xiong.xmock.jacoco.ReportGenerator;
import org.xiong.xmock.jacoco.RestoreMojo;

public class Agent implements IAgent {
    private static Agent singleton;
    private final AgentOptions options;
    private final IExceptionLogger logger;
    private final RuntimeData data;
    private IAgentOutput output;
    private Callable<Void> jmxRegistration;

    public static synchronized Agent getInstance(AgentOptions options) throws Exception {
        if (singleton == null) {
            final Agent agent = new Agent(options, IExceptionLogger.SYSTEM_ERR);
            agent.startup();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    agent.shutdown();
                }
            });
            singleton = agent;
        }

        return singleton;
    }

    public static synchronized Agent getInstance() throws IllegalStateException {
        if (singleton == null) {
            throw new IllegalStateException("JaCoCo agent not started.");
        } else {
            return singleton;
        }
    }

    Agent(AgentOptions options, IExceptionLogger logger) {
        this.options = options;
        this.logger = logger;
        this.data = new RuntimeData();
    }

    public RuntimeData getData() {
        return this.data;
    }

    public void startup() throws Exception {
        try {
            String sessionId = this.options.getSessionId();
            if (sessionId == null) {
                sessionId = this.createSessionId();
            }

            this.data.setSessionId(sessionId);
            this.output = this.createAgentOutput();
            this.output.startup(this.options, this.data);
            if (this.options.getJmx()) {
                this.jmxRegistration = new JmxRegistration(this);
            }

        } catch (Exception var2) {
            this.logger.logExeption(var2);
            throw var2;
        }
    }

    public void shutdown() {
        try {
            if (this.options.getDumpOnExit()) {
                this.output.writeExecutionData(false);
            }

            this.output.shutdown();
            if (this.jmxRegistration != null) {
                this.jmxRegistration.call();
            }

            //报表分析
         //   new ReportGenerator().create();
        } catch (Exception var2) {
            this.logger.logExeption(var2);
        }

    }

    IAgentOutput createAgentOutput() {
        OutputMode controllerType = this.options.getOutput();
        switch(controllerType) {
        case file:
            return new FileOutput();
        case tcpserver:
            return new TcpServerOutput(this.logger);
        case tcpclient:
            return new TcpClientOutput(this.logger);
        case none:
            return new NoneOutput();
        default:
            throw new AssertionError(controllerType);
        }
    }

    private String createSessionId() {
        String host;
        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (Exception var3) {
            host = "unknownhost";
        }

        return host + "-" + AbstractRuntime.createRandomId();
    }

    public String getVersion() {
        return JaCoCo.VERSION;
    }

    public String getSessionId() {
        return this.data.getSessionId();
    }

    public void setSessionId(String id) {
        this.data.setSessionId(id);
    }

    public void reset() {
        this.data.reset();
    }

    public byte[] getExecutionData(boolean reset) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try {
            ExecutionDataWriter writer = new ExecutionDataWriter(buffer);
            this.data.collect(writer, writer, reset);
        } catch (IOException var4) {
            throw new AssertionError(var4);
        }

        return buffer.toByteArray();
    }

    public void dump(boolean reset) throws IOException {
        this.output.writeExecutionData(reset);
    }
}
