package net.sourceforge.cobertura.ant;

import com.sun.tools.corba.se.idl.Compile;
import com.sun.tools.corba.se.idl.toJavaPortable.StringGen;
import net.sourceforge.cobertura.Arguments;
import net.sourceforge.cobertura.Cobertura;
import net.sourceforge.cobertura.util.DirectoryClassLoader;
import org.apache.bcel.classfile.Code;
import org.apache.log4j.Logger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.types.FileSet;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import static net.sourceforge.cobertura.util.ArchiveUtil.getFiles;

public class CodeInstrumentationIntegrationTest {
    private static final Logger log = Logger.getLogger(CodeInstrumentationIntegrationTest.class);

    @Test
    public void testInstrumentUsingDirSet_withoutAnt() throws Throwable {
        File basedir = new File("./src/test/resources/integration/examples/functionaltest1/");
        File instrumented = new File(basedir, "src");
        instrumented.mkdirs();

        compile(basedir, null);//new CompilerOptions().setClassOutputDirectory(new File("./target/cobertura/instrumented"))

        Cobertura cobertura = instrumentCode(instrumented, instrumented);

        //Custom classloader to load instrumented classes
        ClassLoader classLoader = new DirectoryClassLoader(instrumented);

        // Load tests with our own classloader.
        URLClassLoader loader1 = new URLClassLoader(
                new URL[] {instrumented.toURL() }, classLoader);
        loader1.loadClass("test.first.A");
        loader1.loadClass("test.first.B");
        loader1.loadClass("test.second.A");
        loader1.loadClass("test.second.B");
        loader1.loadClass("test.first.RemoteInterface");
        loader1.loadClass("test.first.RemoteListener");
        loader1.loadClass("net.sourceforge.cobertura.coveragedata.HasBeenInstrumented");

        //run tests
        Class testClass = loader1.loadClass("test.first.FirstTest");
        new JUnitCore().run(testClass);

        //get report
        cobertura.buildReport();

        //clean .class files
        List<File>files = new ArrayList<File>();
        getFiles(basedir, ".class", files);
        for(File file:files){
            file.delete();
        }
    }



    /*   Aux methods   */
    private static void compile(File basedir, CompilerOptions compilerOptions){
        /*   Search for java files and compile them   */
        List<File>javaFiles = new ArrayList<File>();
        getFiles(basedir,".java", javaFiles);
        compile(javaFiles.toArray(new File[javaFiles.size()]), compilerOptions);
    }

    private static void compile(File[]files,  CompilerOptions compilerOptions){
       JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
       StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

       Iterable<? extends JavaFileObject> filesToCompile =
           fileManager.getJavaFileObjectsFromFiles(Arrays.asList(files));

        try {
            if(compilerOptions==null){
                compiler.getTask(null, fileManager, null, null, null, filesToCompile).call();
            }else{
                compiler.getTask(null, fileManager, null,compilerOptions.getCompilerOptions() , null, filesToCompile).call();
            }
            fileManager.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Cobertura instrumentCode(File origClassesDir, File intrumentedCodeDestDir) throws Throwable {
        /*   Look for .class files and instrument them   */
        List<File>classFiles = new ArrayList<File>();
        getFiles(origClassesDir,".class", classFiles);
        Arguments args =
                new Arguments().setBaseDirectory(origClassesDir.getAbsolutePath())
                        .setDataFile("cobertura.ser")
                        .setDestinationFile(intrumentedCodeDestDir.getAbsolutePath());

        //TODO see a way to automatically detect test classes and get a list of classes
//        File[]files = classFiles.toArray(new File[classFiles.size()]);
//        List<String>tests = new ArrayList<String>();
//        for(File file : files){
//            args.addFileToInstrument(file.getAbsolutePath());
//            if(file.toString().endsWith("Test.class")){
//                tests.add(file.getCanonicalPath());
//            }
//        }

        return new Cobertura(args).instrumentCode();
    }

    private static class CompilerOptions{
        Map<String, String>options;

        public CompilerOptions(){
            options = new HashMap<String, String>();
        }

        public CompilerOptions setClassOutputDirectory(String dir){
            options.put("-d ", dir);
            return this;
        }

        public CompilerOptions setSourceOutputDirectory(String dir){
            options.put("-s ", dir);
            return this;
        }

        public CompilerOptions generateDebuggingInfo(boolean generate){
            String parameter = "-g";
            if(generate){
                options.put(parameter, "");
            }else{
                options.put(parameter, ":none");
            }
            return this;
        }

        public CompilerOptions verbose(boolean generate){
            String parameter = "-verbose";
            if(generate){
                options.put(parameter, "");
            }else{
                options.remove(parameter);
            }
            return this;
        }

        public Iterable<String> getCompilerOptions(){
            Iterator<Map.Entry<String, String>> entries = options.entrySet().iterator();
            List<String>opts = new ArrayList<String>();
            int j=0;
            Map.Entry<String, String>entry;
            while(entries.hasNext()){
                entry = entries.next();
                opts.add(entry.getKey());
                if(!"".equals(entry.getValue())){
                    opts.add(entry.getValue());
                }
            }
            return opts;
        }
    }

    /*
    possible options include:
  -g                         Generate all debugging info
  -g:none                    Generate no debugging info
  -g:{lines,vars,source}     Generate only some debugging info
  -nowarn                    Generate no warnings
  -verbose                   Output messages about what the compiler is doing
  -deprecation               Output source locations where deprecated APIs are used
  -classpath <path>          Specify where to find user class files and annotation processors
  -cp <path>                 Specify where to find user class files and annotation processors
  -sourcepath <path>         Specify where to find input source files
  -bootclasspath <path>      Override location of bootstrap class files
  -extdirs <dirs>            Override location of installed extensions
  -endorseddirs <dirs>       Override location of endorsed standards path
  -proc:{none,only}          Control whether annotation processing and/or compilation is done.
  -processor <class1>[,<class2>,<class3>...]Names of the annotation processors to run; bypasses default discovery process
  -processorpath <path>      Specify where to find annotation processors
  -d <directory>             Specify where to place generated class files
  -s <directory>             Specify where to place generated source files
  -implicit:{none,class}     Specify whether or not to generate class files for implicitly referenced files
  -encoding <encoding>       Specify character encoding used by source files
  -source <release>          Provide source compatibility with specified release
  -target <release>          Generate class files for specific VM version
  -version                   Version information
  -help                      Print a synopsis of standard options
  -Akey[=value]              Options to pass to annotation processors
  -X                         Print a synopsis of nonstandard options
  -J<flag>                   Pass <flag> directly to the runtime system
     */
}