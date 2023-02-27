package org.JE.JECompiler;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Compiler {

    public static void startCompile(String sourceDir, String outputDir, String javapath, String... dependencies) {
        File input = new File(sourceDir);
        File output = new File(outputDir);
        if (!input.exists() || !input.isDirectory()) {
            throw new IllegalArgumentException("Input folder does not exist or is not a folder");
        }
        if (!output.exists()) {
            output.mkdirs();
        }
        if (!output.isDirectory()) {
            throw new IllegalArgumentException("Output folder is not a folder");
        }
        compile(input, output, javapath, dependencies);
    }

    public static String[] getFiles(File input){
        ArrayList<String> files = new ArrayList<>();
        for (File f : input.listFiles()) {
            if(f.isDirectory()){
                files.addAll(Arrays.asList(getFiles(f)));
            }
            else{
                files.add(f.getAbsolutePath());
            }
        }
        return files.toArray(new String[0]);
    }

    private static void compile(File input, File output, String javapath, String... dependencies) {

        String[] allFiles = getFiles(input);
        ArrayList<String> files = new ArrayList<>();

        for (String file :
                allFiles) {
            // make sure is a java file
            if(file.endsWith(".java")){
                files.add(file);
            }
        }

        ProcessBuilder pb = new ProcessBuilder();
        if(dependencies.length > 0){
            pb.command("javac", "-d",output.getAbsolutePath(), "-cp");
            for (String s : dependencies) {
                pb.command().add(s);
            }
        }
        else{
            pb.command(javapath, "-d",output.getAbsolutePath());
        }

        pb.command().add("-sourcepath");
        pb.command().add(input.getAbsolutePath());
        // add all files to compile
        for (String f : files) {
            pb.command().add(f);
        }

        System.out.println("Compile command:");
        System.out.println(pb.command().toString());

        try {
            Process p = pb.start();
            int code = p.waitFor();
            if(code == 0){
                System.out.println("Successfully compiled all input files to .class.");
            }
            else
            {
                System.out.println("Error code " + code + " occurred during class compilation.\nMake sure your input strings are correct");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
