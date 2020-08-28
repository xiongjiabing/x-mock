package org.xiong.xmock.api.base;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class XspiLoader<S> implements Iterable<S>{

    private static final String PREFIX = "META-INF/xspi/";
    private final Set<String> providers = new HashSet<>();
    private final Class<S> service;
    private final ClassLoader loader;
    private LazyIterator lookupIterator;

    public static <S> XspiLoader<S> load(Class<S> service) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return XspiLoader.load(service, cl);
    }

    public static <S> XspiLoader<S> load(Class<S> service,ClassLoader loader)
    {
        return new XspiLoader<>(service, loader);
    }

    public Set<String> getProviders(){
        return this.providers;
    }

    public ClassLoader getLoader(){
        return this.loader;
    }

    public void reload() {
        providers.clear();
        lookupIterator = new LazyIterator(service, loader);
    }

    public XspiLoader(Class<S> svc, ClassLoader cl) {
        service = Objects.requireNonNull(svc, "Service interface cannot be null");
        loader = (cl == null) ? ClassLoader.getSystemClassLoader() : cl;
        reload();
    }

    public Iterator<S> iterator() {
        return new Iterator<S>() {

            public boolean hasNext() {
                return lookupIterator.hasNext();
            }

            public S next() {
                return lookupIterator.next();
            }
            public void remove() {
                throw new UnsupportedOperationException();
            }

        };
    }


    private int parseLine(Class<?> service, URL u, BufferedReader r, int lc,
                          List<String> names)
            throws IOException, ServiceConfigurationError
    {
        String ln = r.readLine();
        if (ln == null) {
            return -1;
        }
        int ci = ln.indexOf('#');
        if (ci >= 0) ln = ln.substring(0, ci);
        ln = ln.trim();
        int n = ln.length();
        if (n != 0) {
//            if ((ln.indexOf(' ') >= 0) || (ln.indexOf('\t') >= 0))
//                fail(service, u, lc, "Illegal configuration-file syntax");
//            int cp = ln.codePointAt(0);
//            if (!Character.isJavaIdentifierStart(cp))
//                fail(service, u, lc, "Illegal provider-class name: " + ln);
//            for (int i = Character.charCount(cp); i < n; i += Character.charCount(cp)) {
//                cp = ln.codePointAt(i);
//                if (!Character.isJavaIdentifierPart(cp) && (cp != '.'))
//                    fail(service, u, lc, "Illegal provider-class name: " + ln);
//            }
            if ( !names.contains(ln) )
                names.add(ln);
        }
        return lc + 1;
    }


    public Iterator<String> parse(Class<?> service, URL u)
            throws ServiceConfigurationError
    {
        InputStream in = null;
        BufferedReader r = null;
        ArrayList<String> names = new ArrayList<>();
        try {
            in = u.openStream();
            r = new BufferedReader(new InputStreamReader(in, "utf-8"));
            int lc = 1;
            while ((lc = parseLine(service, u, r, lc, names)) >= 0);
        } catch (IOException x) {
            fail(service, "Error reading configuration file", x);
        } finally {
            try {
                if (r != null) r.close();
                if (in != null) in.close();
            } catch (IOException y) {
                fail(service, "Error closing configuration file", y);
            }
        }

       return new ArrayList<String>(){{
            this.add(StringUtils.join(names,","));
        }}.iterator();
    }

    private class LazyIterator
            implements Iterator<S>
    {

        Class<S> service;
        ClassLoader loader;
        Enumeration<URL> configs = null;
        Iterator<String> pending = null;
        String nextName = null;

        private LazyIterator(Class<S> service, ClassLoader loader) {
            this.service = service;
            this.loader = loader;
        }

        private boolean hasNextService() {
            if (nextName != null) {
                return true;
            }
            if (configs == null) {
                try {
                    String fullName = PREFIX + service.getName();
                    if (loader == null)
                        configs = ClassLoader.getSystemResources(fullName);
                    else
                        configs = loader.getResources(fullName);
                } catch (IOException x) {
                    fail(service, "Error locating configuration files", x);
                }
            }
            while ((pending == null) || !pending.hasNext()) {
                if (!configs.hasMoreElements()) {
                    return false;
                }
                pending = parse(service, configs.nextElement());
            }
            nextName = pending.next();
            return true;
        }

        private S nextService() {
            if (!hasNextService())
                throw new NoSuchElementException();
            String cn = nextName;
            nextName = null;
            Class<?> c = null;

            if(StringUtils.isBlank( cn )){
                throw new NoSuchElementException("spi schema name is null");
            }

            if( providers.add( cn)){
                return null;
            }
            throw new Error();          // This cannot happen
        }

        public boolean hasNext() {
                return hasNextService();
        }

        public S next() {
            return nextService();
        }

        public void remove() {
            throw new UnsupportedOperationException();
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
