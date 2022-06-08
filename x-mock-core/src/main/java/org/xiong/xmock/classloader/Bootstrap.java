/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xiong.xmock.classloader;


import lombok.SneakyThrows;
import org.xiong.xmock.classloader.archive.Archive;
import org.xiong.xmock.classloader.archive.JarFileArchive;
import org.xiong.xmock.classloader.jar.JarFile;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class Bootstrap {

    private static final Pattern PATH_PATTERN = Pattern.compile("(\".*?\")|(([^,])*)");

    private static final String scanJarPath = "\"/xmock/lib/external/*.jar\",\"/xmock/lib/*.jar\"";

    @SneakyThrows
    public static ClassLoader createClassLoader(ClassLoader parent) {

        List<ClassLoaderFactory.Repository> repositories = new ArrayList<>();

        String[] repositoryPaths = getPaths(scanJarPath);

        for (String repository : repositoryPaths) {
            // Check for a JAR URL repository
            try {
                URL url = new URL(repository);
                repositories.add(
                        new ClassLoaderFactory.Repository(repository, ClassLoaderFactory.RepositoryType.URL));
                continue;
            } catch (Exception e) {
                // Ignore
            }

            // Local repository
            if (repository.endsWith("*.jar")) {
                repository = repository.substring
                    (0, repository.length() - "*.jar".length());
                repositories.add(
                        new ClassLoaderFactory.Repository(repository, ClassLoaderFactory.RepositoryType.GLOB));
            } else if (repository.endsWith(".jar")) {
                repositories.add(
                        new ClassLoaderFactory.Repository(repository, ClassLoaderFactory.RepositoryType.JAR));
            } else {
                repositories.add(
                        new ClassLoaderFactory.Repository(repository, ClassLoaderFactory.RepositoryType.DIR));
            }
        }

        return ClassLoaderFactory.createClassLoader(repositories, parent);
    }



    // Copied from ExceptionUtils since that class is not visible during start
    private static void handleThrowable(Throwable t) {
        if (t instanceof ThreadDeath) {
            throw (ThreadDeath) t;
        }
        if (t instanceof VirtualMachineError) {
            throw (VirtualMachineError) t;
        }
        // All other instances of Throwable will be silently swallowed
    }


    // Protected for unit testing
    protected static String[] getPaths(String value) {

        List<String> result = new ArrayList<>();
        Matcher matcher = PATH_PATTERN.matcher(value);

        while (matcher.find()) {
            String path = value.substring(matcher.start(), matcher.end());

            path = path.trim();
            if (path.length() == 0) {
                continue;
            }

            char first = path.charAt(0);
            char last = path.charAt(path.length() - 1);

            if (first == '"' && last == '"' && path.length() > 1) {
                path = path.substring(1, path.length() - 1);
                path = path.trim();
                if (path.length() == 0) {
                    continue;
                }
            } else if (path.contains("\"")) {
                // Unbalanced quotes
                // Too early to use standard i18n support. The class path hasn't
                // been configured.
                throw new IllegalArgumentException(
                        "The double quote [\"] character only be used to quote paths. It must " +
                        "not appear in a path. This loader path is not valid: [" + value + "]");
            } else {
                // Not quoted - NO-OP
            }

            result.add(path);
        }
        return result.toArray(new String[result.size()]);
    }


    @SneakyThrows
    public static ClassLoader createXmockClassLoader(ClassLoader parent){
        File root = getRootFile();

        if(root.isDirectory()){
            return createClassLoader(parent);
        }
        JarFile.registerUrlProtocolHandler();
        Archive archive = createArchive(root);
        Iterator<Archive> iterator = getClassPathArchivesIterator(archive);
        return createClassLoader(archive,iterator,parent);
    }


    private static final File getRootFile() throws Exception{
        ProtectionDomain protectionDomain = Bootstrap.class.getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();
        URI location = (codeSource != null) ? codeSource.getLocation().toURI() : null;
        String path = (location != null) ? location.getSchemeSpecificPart() : null;
        if (path == null) {
            throw new IllegalStateException("Unable to determine code source archive");
        }
        File root = new File(path);
        if (!root.exists()) {
            throw new IllegalStateException("Unable to determine code source archive from " + root);
        }
        return root;
    }


    private static final Archive createArchive(File root) throws Exception {
        //return (root.isDirectory() ?  new ExplodedArchive(root) : new JarFileArchive(root));
        return new JarFileArchive(root);

    }

    protected static ClassLoader createClassLoader(Archive root,Iterator<Archive> archives,ClassLoader parent) throws Exception {
        List<URL> urls = new ArrayList<>(50);
        while (archives.hasNext()) {
            urls.add(archives.next().getUrl());
        }
        urls = urls.stream().filter(url -> url.getPath().endsWith(".jar!/")).collect(Collectors.toList());

        List<URL> finalUrls = urls;
        return AccessController.doPrivileged(
                new PrivilegedAction<URLClassLoader>() {
                    @Override
                    public URLClassLoader run() {
                        if (parent == null)
                            return new XMockURLClassLoader(finalUrls.toArray(new URL[0]),ClassLoader.getSystemClassLoader());
                        else
                            return new XMockURLClassLoader(root.isExploded(), root, finalUrls.toArray(new URL[0]), parent);
                    }
                });
    }

    protected static Iterator<Archive> getClassPathArchivesIterator(Archive archive) throws Exception {
        Archive.EntryFilter searchFilter = Bootstrap::isSearchCandidate;
        Iterator<Archive> archives = archive.getNestedArchives(searchFilter,
                (entry) -> entry.getName().startsWith("xmock/lib/"));
//        if (isPostProcessingClassPathArchives()) {
//            archives = applyClassPathArchivePostProcessing(archives);
//        }
        return archives;
    }

    protected static boolean isSearchCandidate(Archive.Entry entry) {
        return true;
    }
}
