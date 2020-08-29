package org.xiong.xmock.api.base;
import java.util.*;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class SchemaItemManager {

    static final Map<String, List<SchemaItem>> testClassMapping = new HashMap<>();

    public static Map<String,Map<String,SchemaItem>> obtainActualSchemaItem( String testClassName, String testScope ){
        Map<String,Map<String,SchemaItem>> mockClassMapping = new HashMap<>();

        Optional.ofNullable( testClassMapping.get( testClassName ).stream().filter(r->{
            return isBlank( r.getService() );

        })).ifPresent( schemaItems->{schemaItems.forEach( item->{

                if( isBlank( item.getTestScope() ) || item.getTestScope().contains( testScope ) ){
                    Map<String,SchemaItem> schemaItemMap = mockClassMapping.get( item.getMockTargetClassSimpleName());
                    if( schemaItemMap == null ){
                        schemaItemMap = new HashMap<>();
                        mockClassMapping.put( item.getMockTargetClassSimpleName() ,schemaItemMap );
                    }
                    schemaItemMap.put( item.getMockTargetMethodName() , item );
                }
            });
        });
        return mockClassMapping;
    }

    public static Map<String,String> getServiceName( String testClassName ){
        Map<String,String> serverMap = new HashMap<>();
        Optional.ofNullable(
                testClassMapping.get( testClassName ).stream().filter(r->{
                    return !isBlank( r.getService() );

        })).ifPresent( schemaItems->{

            schemaItems.forEach( item->{
                String serviceTag = item.getService();
                String[] serviceArr = serviceTag.split("=");
                String [] serviceNames = serviceArr[1].split("_");

                for ( String name : serviceNames ) {
                    serverMap.putIfAbsent( name, serviceArr[0]);
                }

            });
        });
        return serverMap;
    }

    public static void addTestClassMapping(String testClass, List<SchemaItem> items ){
        testClassMapping.put(testClass, items );
    }
}
