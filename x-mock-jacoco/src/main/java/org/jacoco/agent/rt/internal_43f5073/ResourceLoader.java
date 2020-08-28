package org.jacoco.agent.rt.internal_43f5073;

import org.xiong.xmock.jacoco.XInstrument;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ResourceLoader {

    public static Properties loadResource( String resource ) {
        Properties result = new Properties();
        InputStream file = XInstrument.class.getResourceAsStream(resource);
        if (file != null) {
            try {
                result.load(file);
            } catch (IOException var4) {
                throw new RuntimeException(var4);
            }
        }
        return result;
    }
}
