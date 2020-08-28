//package org.xiong.xmock.jacoco;
//
//
//import com.google.auto.service.AutoService;
//import com.sun.source.util.Trees;
//import com.sun.tools.javac.code.Flags;
//import com.sun.tools.javac.code.Type;
//import com.sun.tools.javac.processing.JavacProcessingEnvironment;
//import com.sun.tools.javac.tree.JCTree;
//import com.sun.tools.javac.tree.TreeMaker;
//import com.sun.tools.javac.util.List;
//import com.sun.tools.javac.util.Name;
//import com.sun.tools.javac.util.Names;
//
//import javax.annotation.processing.*;
//import javax.lang.model.SourceVersion;
//import javax.lang.model.element.Element;
//import javax.lang.model.element.TypeElement;
//import java.util.Set;
//
//@AutoService(Processor.class)
//@SupportedAnnotationTypes("*")
//@SupportedSourceVersion(SourceVersion.RELEASE_8)
//public class XmockInstrumentProcessor extends AbstractProcessor {
//
//        private JavacProcessingEnvironment env;
//        private Trees trees;
//        private TreeMaker treeMaker;
//        private Names names;
//
//        @Override
//        public void init(ProcessingEnvironment processingEnv) {
//            super.init(processingEnv);
//            this.trees = Trees.instance(processingEnv);
//            this.env = (JavacProcessingEnvironment) processingEnv;
//            this.treeMaker = TreeMaker.instance(env.getContext());
//            this.names = Names.instance(env.getContext());
//        }
//
//        @Override
//        public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
//            if (roundEnv.processingOver()) {
//                return true;
//            }
//
//           // Runtime.getRuntime().
//            for (Element element : roundEnv.getElementsAnnotatedWith(Instrument.class)) {
//                if (!(element instanceof TypeElement)) continue;
//
//            }
//            return true;
//        }
//
//
//
//}
