package org.xiong.xmock.idl;
import org.xiong.xmock.api.base.SchemaItem;
import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.sql.DriverManager.println;

public class YamlProcessor {

    public List<SchemaItem> loadYamlOnServer(String fileName) {
        List<SchemaItem> items = new ArrayList<>();
        InputStream in = null;
        try {
            Yaml yaml = new Yaml();
            in = getResource(fileName);
            if (in == null) {
                println("["+fileName+"]file not found ");
                return null;
            }
            Object result = yaml.load(in);
            if (!(result instanceof List)) {
                println("invalid mock file, not a list");
                return null;
            }

            boolean isReturn = false;
            List l = (List) result;
            for (Object o : l) {
                if (!(o instanceof Map)) {
                    println("invalid mock file, not a map, line="+o);
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

                s = m.get("type");
                String type = s != null ? s.toString() : "";

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
                i.setType(type);
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
            println("mock file load exception:"+e.getMessage());
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
            println("["+mockFie+"] file not found ");
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
                println("["+fileName+"] file not found ");
                return null;
            }
            Object result = yaml.load(in);
            if (!(result instanceof List)) {
                println("invalid mock file, not a list");
                return null;
            }

            boolean isReturn = false;
            List l = (List) result;
            for (Object o : l) {
                if (!(o instanceof Map)) {
                    println("invalid mock file, not a map, line="+o);
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
                    println("invalid mock file, not a map, line="+o);
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

                s = m.get("type");
                String type = s != null ? s.toString() : "";

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
                i.setType(type);
                i.setSleep( sleep );
                i.setThrowError( throwError );
                i.setTestScope(test);
                i.setField( field );
                i.setRes(res);
                items.add(i);
            }
            return items;
        } catch (Exception e) {
            println("mock file load exception:"+e.getMessage());
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
