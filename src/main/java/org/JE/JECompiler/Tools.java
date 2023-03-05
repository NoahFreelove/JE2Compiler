package org.JE.JECompiler;

import org.JE.JE2.Window.WindowPreferences;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class Tools {
    public static volatile AtomicInteger compileStep = new AtomicInteger(0);
    public static void startCompileChain(String ABSOLUTE_sceneFolder, String ABSOLUTE_runPath, String runPackage, WindowPreferences windowPreferences,
                                         String ABSOLUTE_src, String ABSOLUTE_output, String ABSOLUTE_javaCompileCommandOrPath,
                                         String ABSOLUTE_JE2JarLocation, String outputJarName, String ABSOLUTE_jarCompileCommandOrPath, String... additionalDependencies){
        compileStep.set(0);
        // Scene folder
        // run path (src/main/java/org/something
        // run package (org.something)
        // Window preferences
        // absolute location of src folder
        // absolute location of output folder
        // javac command / exe path
        // JE2.jar absolute path
        // Output jar name
        // jar command / exe path
        if(ABSOLUTE_src.endsWith("/")){
            ABSOLUTE_src = ABSOLUTE_src.substring(0, ABSOLUTE_src.length()-1);
        }
        if(ABSOLUTE_src.endsWith("\\")){
            ABSOLUTE_src = ABSOLUTE_src.substring(0, ABSOLUTE_src.length()-1);
        }
        WorldBuilder.buildAllWorlds(ABSOLUTE_sceneFolder, ABSOLUTE_runPath, runPackage, windowPreferences);
        compileStep.incrementAndGet();

        // Add ABSOLUTE_JE2JarLocation to front of additionalDependencies array
        String[] newAdditionalDependencies = new String[additionalDependencies.length + 1];
        newAdditionalDependencies[0] = ABSOLUTE_JE2JarLocation;
        for (int i = 0; i < additionalDependencies.length; i++) {
            newAdditionalDependencies[i + 1] = additionalDependencies[i];
        }

        Compiler.startCompile(ABSOLUTE_src, ABSOLUTE_output, ABSOLUTE_javaCompileCommandOrPath, newAdditionalDependencies);
        Compiler.mergeResources(ABSOLUTE_src+"\\main\\resources", ABSOLUTE_output);
        compileStep.incrementAndGet();
        JarCreator.toJar(ABSOLUTE_output, outputJarName, runPackage, ABSOLUTE_jarCompileCommandOrPath, newAdditionalDependencies);
        compileStep.set(3);
    }

    public static void startCompileChain(String sceneFolder, String runPath, String runPackage, WindowPreferences windowPreferences,
                                         String ABSOLUTE_src, String ABSOLUTE_output, File javaCompileCommand,
                                         File ABSOLUTE_JE2JarLocation, String outputJarName, File jarCompileCommand){
        startCompileChain(sceneFolder, runPath, runPackage, windowPreferences, ABSOLUTE_src, ABSOLUTE_output,
                javaCompileCommand.getAbsolutePath(), ABSOLUTE_JE2JarLocation.getAbsolutePath(), outputJarName, jarCompileCommand.getAbsolutePath());
    }

    public static void simpleCompile(String sceneFolder, String runPath, String runPackage, WindowPreferences windowPreferences,
                                     String ABSOLUTE_src, String ABSOLUTE_output, String ABSOLUTE_javaCompileCommandOrPath,
                                     String ABSOLUTE_JE2JarLocation){
        compileStep.set(0);
        WorldBuilder.buildAllWorlds(sceneFolder,runPath, runPackage, windowPreferences);
        compileStep.incrementAndGet();
        Compiler.startCompile(ABSOLUTE_src, ABSOLUTE_output, ABSOLUTE_javaCompileCommandOrPath, ABSOLUTE_JE2JarLocation);
        compileStep.set(3);
    }

    public static boolean isFinishedCompiling(){
        return compileStep.get() == 3;
    }
}
