package org.compiler;

import java.io.File;
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

    private String[] getFiles(File input){
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

        ArrayList<String> files = new ArrayList<>(Arrays.asList(new Compiler().getFiles(input)));
        System.out.println(files.size());

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
        System.out.println(pb.command().toString());

        try {
            Process p = pb.start();
            int code = p.waitFor();
            System.out.println(code);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
