package org.xiong.xmock.idl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xiong.xmock.api.base.SchemaItem;
import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YamlProcessor {
    static Logger log = LoggerFactory.getLogger(YamlProcessor.class);

    public List<SchemaItem> loadYamlOnServer(String fileName) {
        List<SchemaItem> items = new ArrayList<>();
        InputStream in = null;
        try {
            Yaml yaml = new Yaml();
            in = getResource(fileName);
            if (in == null) {
                log.warn("[{}] file not found ",fileName);
                return null;
            }
            Object result = yaml.load(in);
            if (!(result instanceof List)) {
                log.error("invalid mock file, not a list");
                return null;
            }

            boolean isReturn = false;
            List l = (List) result;
            for (Object o : l) {
                if (!(o instanceof Map)) {
                    log.error("invalid mock file, not a map, line="+o);
                    continue;
                }
                Map m = (Map) o;
                Object s = m.get("for-return");
                if ( s != null ) {
                    isReturn = true;
                }else{
                    s = m.get("for-field");
                }

                String forName = s != null ? s.toString() : "";
                s = m.get("test");
                String test = s != null ? s.toString() : "";

                s = m.get("for-service");
                String service = s != null ? s.toString() : "";

                s = m.get("sleep");
                String sleep = s != null ? s.toString() : null;

                s = m.get("throw");
                String throwError = s != null ? s.toString() : null;



                Object res = m.get("return");
                Object field = m.get("field");

                SchemaItem i = new SchemaItem();
                i.setReturn( isReturn );
                i.setForName(forName);
                i.setService(service);
                i.setSleep( sleep );
                i.setThrowError( throwError );
                i.setTestScope(test);
                i.setField(field);
                i.setRes(res);
                items.add(i);
            }
            return items;
        } catch (Exception e) {
            log.error("mock file load exception", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

    public boolean mockFileIsexist(String mockFie ){
        InputStream in = null;
        in = getResource(mockFie);
        if (in == null) {
            log.warn("[{}] file not found ",mockFie);
            return false;
        }
        return true;
    }

    public List<SchemaItem> loadYamlOnClient(String fileName ,String testCaseScope ) {
        List<SchemaItem> items = new ArrayList<>();

        InputStream in = null;
        try {
            Yaml yaml = new Yaml();
            in = getResource(fileName);
            if (in == null) {
                log.warn("[{}] file not found ",fileName);
                return null;
            }
            Object result = yaml.load(in);
            if (!(result instanceof List)) {
                log.error("invalid mock file, not a list");
                return null;
            }

            boolean isReturn = false;
            List l = (List) result;
            for (Object o : l) {
                if (!(o instanceof Map)) {
                    log.error("invalid mock file, not a map, line="+o);
                    continue;
                }
                Map m = (Map) o;
                Object s = m.get("for-return");
                if (s == null) {
                    s = m.get("for-field");
                }else{
                    isReturn = true;
                }
                if (s == null) {
                    log.error("invalid mock file, for not found, line="+o);
                    continue;
                }

                String forName = s.toString();
                s = m.get("test");
                String test = null;
               if( s != null ){
                   test = s.toString();
                   if( !test.contains( testCaseScope ) ){
                       continue;
                   }
               }

                s = m.get("for-service");
                String service = s != null ? s.toString() : "";

                s = m.get("sleep");
                String sleep = s != null ? s.toString() : null;

                s = m.get("throw");
                String throwError = s != null ? s.toString() : null;

                Object res = m.get("return");
                Object field = m.get("field");

                SchemaItem i = new SchemaItem();
                i.setReturn( isReturn );
                i.setService( service );
                i.setForName( forName );
                i.setSleep( sleep );
                i.setThrowError( throwError );
                i.setTestScope(test);
                i.setField( field );
                i.setRes(res);
                items.add(i);
            }
            return items;
        } catch (Exception e) {
            log.error("mock file load exception", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

    InputStream getResource(String file) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
    }
}
