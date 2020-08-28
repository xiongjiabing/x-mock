package org.xiong.xmock.jacoco;
import org.xiong.xmock.api.CodeCoverage;

public class JacocoMain implements CodeCoverage {
    
    private XInstrument xInstrument = new XInstrument();

    @Override
    public void start() {
        try {
            xInstrument.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        xInstrument = null;
    }
}
