package org.xiong.xmock.jacoco;
import java.io.*;
import java.util.*;
import com.sun.tools.javac.Main;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.jacoco.agent.rt.internal_43f5073.ResourceLoader;
import org.jacoco.core.instr.Instrumenter;
import org.jacoco.core.runtime.OfflineInstrumentationAccessGenerator;

import static java.sql.DriverManager.println;


@Instrument
public class XInstrument {
    final String sourceBasePath = "src/main/java/";

    public void execute()
            throws Exception {
        //AgentOptions

        final File originalClassesDir = new File(
                "target",
                "generated-classes/jacoco");

        if( originalClassesDir.exists() ){
            originalClassesDir.delete();
        }else {
            originalClassesDir.mkdirs();
        }

        final File classesDir = new File("target/classes");
        if (!classesDir.exists()) {
            println( "missing target.classes directory:"+classesDir);
            return;
        }
        //org/xiong/xmock/jacoco/learn/Car.class
        final List<String> fileNames;
        try {
            List<String> excludes = new ArrayList<>();
            Properties config = ResourceLoader.loadResource("/jacoco-agent.properties");
            String includesStr = config.get("includes").toString();
            String [] excludeArr = StringUtils.split(includesStr,",");
            for (int i = 0; i < excludeArr.length; i++) {
                excludes.add(excludeArr[i]);
            }

            fileNames = new FileFilter( null, excludes )
                    .getFileNames(classesDir);
        } catch (final IOException e1) {
            throw new Exception(
                    "Unable to get list of files to instrument.", e1);
        }

        final File sourceDir = new File("src/main/java");
        if (!sourceDir.exists()) {
            println("missing src.main.java directory:"+sourceDir);
            return;
        }

//        final List<String> sourceFileNames;
//        try {
//            sourceFileNames = new FileFilter(null, null )
//                    .getFileNames(sourceDir);
//        } catch (final IOException e1) {
//            throw new Exception(
//                    "Unable to get list of files to source pathName.", e1);
//        }

        final Instrumenter instrumenter = new Instrumenter(
                new OfflineInstrumentationAccessGenerator());
       // File file = new File("template");
        final File file = new File(
                "target",
                "x-mock/template");

        FileInfo fileInfo = new FileInfo();
        Map<String,String> map = fileInfo.getMap();
        if(file.exists()) {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            fileInfo = (FileInfo) ois.readObject();
            map = fileInfo.getMap();
            fileInfo.setMap( map );
        }

//         boolean reportExistsed = new File(
//                "target",
//                "coverage-report").exists();
        for (final String fileName : fileNames) {
            if (fileName.endsWith(".class")) {
                final File source = new File(classesDir, fileName);
                final File backup = new File(originalClassesDir, fileName);
                InputStream input = null;
                OutputStream output = null;
                try {

                    String lastModified = source.lastModified()+"";
                    if( lastModified.equals( map.get(fileName)) ){
                        continue;
                    }
//                    if( reportExistsed ) {
//                        //报表生成过, 需要手工编译，否则会和jacoco植入的class引起冲突
//                        instrumentPre( sourceFileNames, fileName );
//                    }
                    FileUtils.copyFile( source, backup );
                    input = new FileInputStream(backup);
                    output = new FileOutputStream(source);
                    instrumenter.instrument(input, output, source.getPath());

                    map.put( fileName ,source.lastModified()+"");
                } catch (final Exception e2) {
                    throw new Exception(
                            "Unable to instrument file.", e2);
                } finally {
                    IOUtil.close(input);
                    IOUtil.close(output);
                }
            }
        }

        ObjectOutputStream oos = null;
        try{
            if(file.exists()){
                file.delete();
            }
            file.getParentFile().mkdir();
            file.createNewFile();
            oos = new ObjectOutputStream(new FileOutputStream("target/x-mock/template"));
            oos.writeObject(fileInfo);
        }catch (IOException e){
            println("create x-mock file error "+e.getMessage());
        }finally {
            oos.close();
        }
    }

    private void instrumentPre( List<String> sourceFileNames, String fileName ) {
        StringBuilder partClassName = new StringBuilder();

        if( fileName.contains("$") ){
            partClassName.append( StringUtils.substring(fileName,0,fileName.indexOf("$")) );
        }else{
            partClassName.append (StringUtils.substring(fileName,0,fileName.lastIndexOf(".")));
        }
        sourceFileNames.stream().filter( sourceName->{
            return sourceName.contains( partClassName.toString() );
        }).forEach( name->{
            int c = Main.compile(new String[]{ sourceBasePath+name , "-d", "target/classes"});
            if( c != 0 ){
                println("编译失败,编译码:"+c);
                throw new RuntimeException(
                        "编译失败,编译码: "+c);
            }
        });
    }
}
