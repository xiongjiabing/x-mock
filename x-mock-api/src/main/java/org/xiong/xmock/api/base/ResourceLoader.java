package org.xiong.xmock.api.base;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ResourceLoader {

    private static Properties properties;

    public static Properties getSystemProperties(){
        if( properties == null )
              properties = loadResource("/bootstrap.properties");
        return properties;
    }

    public static Properties loadResource( String resource ) {
        Properties result = new Properties();
        InputStream file = Thread.currentThread().getContextClassLoader().getResourceAsStream( resource );
        if ( file != null ) {
            try {
                result.load(file);
            } catch (IOException var4) {
                throw new RuntimeException(var4);
            }
        }
        return result;
    }
}
