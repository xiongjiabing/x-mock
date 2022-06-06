package org.xiong.xmock.jacoco;
import lombok.SneakyThrows;
import org.xiong.xmock.api.CodeCoverage;
import org.xiong.xmock.api.base.TestCaseMetadata;

import java.lang.reflect.Method;

public class JacocoMain implements CodeCoverage {
    
    private XInstrument xInstrument = new XInstrument();

    @Override
    public void start() {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @SneakyThrows
                public void run() {
                    //报表分析
                    reportCreate();
                    //new ReportGenerator().create();
                }
            });
            xInstrument.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        xInstrument = null;
    }

    @SneakyThrows
    private void reportCreate(){
        Class startupClass = TestCaseMetadata.xmockClassLoader.loadClass("org.xiong.xmock.jacoco.ReportGenerator");
        Object startupInstance = startupClass.getConstructor().newInstance();

        Method method =
                startupInstance.getClass().getMethod("create");
        method.invoke(startupInstance,null);
    }

}
