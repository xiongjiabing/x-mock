package org.xiong.xmock.api.base;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.concurrent.ConcurrentHashMap;
import static java.sql.DriverManager.println;
import static org.apache.commons.lang3.StringUtils.isBlank;

public final class ContainerManger {
    private static volatile java.io.PrintWriter logWriter = null;
    public static final ArrayList<Class<?>> SPI_INF = new ArrayList<Class<?>>();

    static {

        List<String> excludeLoads = null;
        Properties properties = ResourceLoader.getSystemProperties();
        if( properties != null ){

            String xmockLoad = (String)properties.get("xmock.load.exclude");
            if( !isBlank(xmockLoad) ) {
                String [] xlods = xmockLoad.split(",");
                excludeLoads = Arrays.asList( xlods );

            }
        }

        List<Class> classList = ClassScanner.scanByAnno( Xspi.class,"org.xiong.xmock.api" );
        if( classList != null ){
            List<String> finalLoads = excludeLoads;
            classList = classList.stream()
                .filter(cls->{
                    Xspi xspi = (Xspi)cls.getAnnotation(Xspi.class);
                    return finalLoads == null || !finalLoads.contains(xspi.name());
                })
               .sorted(Comparator.comparing( cls->{
                Xspi xspi = (Xspi)cls.getAnnotation(Xspi.class);
                return xspi.value();
            })).collect(Collectors.toList());

            for ( Class cls :classList ) {
                if( !Container.class.isAssignableFrom(cls ))
                    fail(cls,cls.getName()+" is not container subclass ");

                  SPI_INF.add(cls);
            }
        }
    }

    static Class<?> getParent( Container childContainer){
        for ( Class cls :SPI_INF ) {
            if ( cls.isAssignableFrom(childContainer.getClass())){
               return cls;
            }
        }
        return null;
    }

    private final static Map<String,Container> containers = new ConcurrentHashMap<>();

    public static void register( Container childContainer ){

        Xspi xspi = getParent(childContainer).getAnnotation(Xspi.class);
        containers.putIfAbsent( xspi.name(),childContainer );
    }

    public static <T> T getContainer(Class<T> cls){
        Xspi xspi = cls.getAnnotation( Xspi.class );
        return (T)containers.get(xspi.name());
    }

    public static void start() throws Exception {
        if( containers.size() == 0 ){
            throw new IllegalAccessException("container not found, can not be start");
        }

        try {
            for ( Container container : containers.values() ) {
                if( container == null )
                    continue;

                container.start();
                println("["+container.getClass()+"] spi service started ");
            }
        }catch (Exception e){
            throw new Exception("the container start error ",e);
        }
    }

    public static void close() throws Exception {
        if( containers.size() == 0 ){
            return;
        }
        try {
            for ( Container container : containers.values() ) {
                if( container == null )
                    continue;

                container.close();
            }
            containers.clear();
            SPI_INF.clear();
        }catch (Exception e){
            throw new Exception("the container close error ",e);
        }
    }


    public static<T> T loadContainerBySpi( Class<T> service){

        XspiLoader<T> loadContainers = XspiLoader.load( service );
        Iterator<T> containersIterator = loadContainers.iterator();
        try{
            while(containersIterator.hasNext()) {
                containersIterator.next();
            }
        } catch(Throwable t) {
            println("load spi error :"+t.getMessage());
            // Do nothing
        }

        return initService( loadContainers,service);
    }


    static<T> T initService( XspiLoader<T>  xspiLoader,Class<T> service) {
        TreeMap<Integer, XspiCnameMapping> treeMap = new TreeMap<>();

        Set<String> providers = xspiLoader.getProviders();
        if( providers.size() == 0 )
            return null;

        for (String schemaName : providers) {

            XspiCnameMapping xspiCnameMapping = new XspiCnameMapping();
            Stream.of(schemaName.split(",")).forEach(value -> {
                dealSchemaName(value, xspiCnameMapping);
            });
            treeMap.put( xspiCnameMapping.getIndex(), xspiCnameMapping );
        }

        XspiCnameMapping firstMapping = treeMap.firstEntry().getValue();
        String cn = firstMapping.getValue();
        Class<?> c = null;
        try {
            println("Initializing spi service ["+firstMapping.getName()+"]");
            c = Class.forName(cn, true, xspiLoader.getLoader());
        } catch (ClassNotFoundException x) {
            fail(service,
                    "Provider " + cn + " not found");
        }
        if (!service.isAssignableFrom(c)) {
            fail(service,
                    "Provider " + cn + " not a subtype");
        }
        try {
            return service.cast(c.newInstance());
        } catch (Throwable x) {
            fail(service,
                    "Provider " + cn + " could not be instantiated",
                    x);
        }

        return null;
    }

    static void dealSchemaName( String value ,XspiCnameMapping xspiCnameMapping ){
        String[] arr = value.split("=");
        String leftV = arr[0].trim();
        String rightV = arr[1].trim();

        if("name".equals( leftV )){

            xspiCnameMapping.setName(rightV);
        }else if("value".equals( leftV )){

            xspiCnameMapping.setValue( rightV );
        }else if( "ordered".equals( leftV )){
            xspiCnameMapping.setIndex( Integer.parseInt(rightV));
        }
    }

   static class XspiCnameMapping{

        private String name;
        private Integer index = 0;
        private String value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    private static void fail(Class<?> service, String msg, Throwable cause)
            throws ServiceConfigurationError
    {
        throw new ServiceConfigurationError(service.getName() + ": " + msg,
                cause);
    }

    private static void fail(Class<?> service, String msg)
            throws ServiceConfigurationError
    {
        throw new ServiceConfigurationError(service.getName() + ": " + msg);
    }

    private static void fail(Class<?> service, URL u, int line, String msg)
            throws ServiceConfigurationError
    {
        fail(service, u + ":" + line + ": " + msg);
    }

}
