package org.JECompiler;

import java.io.*;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarCreator {
    public static boolean logJarCompilation = false;
    public static void toJar(String rootDir, String jarName, String JEMainPackage, String jarExePath, String... dependencies){
        System.out.println("Now compiling to jar...");
        ProcessBuilder pb = new ProcessBuilder();
        // do that but start from rootDir directory

        pb.directory(new File(rootDir));
        System.out.println("Merging Dependencies");

        mergeDependencies(rootDir, dependencies);
        // create manifest file
        File manifest = new File(rootDir + "/Manifest.txt");
        // Create a buffered writer to write to the file
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(manifest));
            writer.write("Main-Class: " + JEMainPackage + ".JEMain\n");
            writer.close();

        }catch (Exception ignore){}
        pb.command(jarExePath, "cfvm",jarName,manifest.getAbsolutePath(), "-C", new File(rootDir).getAbsolutePath(), ".");
        if(logJarCompilation){
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        }
        else {
            pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
            pb.redirectError(ProcessBuilder.Redirect.DISCARD);
        }

        System.out.println("Jar creation command:");
        System.out.println(pb.command().toString());

        try {
            if (!logJarCompilation)
            {
                System.out.println("Working...");
            }
            Process p = pb.start();
            int code = p.waitFor();
            if(code == 0)
            {
                System.out.println("Jar was successfully created at: " + rootDir + "\\" + jarName);
            }
            else
            {
                System.out.println("There was an error during Jar Creation.\nMake sure your inputs are correct.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void mergeDependencies(String mergeFolderPath, String... dependencies){
        for (String path :
                dependencies) {
            try {
                System.out.println("Jar: " + path);
                System.out.println("Merged to: " + mergeFolderPath);
                unzipJar(path,mergeFolderPath);
            }
            catch (IOException e){
                System.out.println("Error merging dependency: " + path);
                System.out.println("Err: " + e.getMessage());
            }
        }
    }

    public static void unzipJar(String jarFilePath, String mergeFolderPath) throws IOException {
        JarFile jarFile = new JarFile(jarFilePath);
        Enumeration<JarEntry> entries = jarFile.entries();

        while (entries.hasMoreElements()) {
            try {
                JarEntry entry = entries.nextElement();
                File file = new File(mergeFolderPath, entry.getName());

                if (entry.isDirectory()) {
                    if(entry.getName().equals("META-INF"))
                        continue;
                    file.mkdirs();
                } else {
                    InputStream is = jarFile.getInputStream(entry);
                    OutputStream os = new FileOutputStream(file);
                    byte[] buffer = new byte[4096];
                    int bytesRead;

                    while ((bytesRead = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }

                    is.close();
                    os.close();
                }
            }
            catch (Exception ignore){}
        }

        jarFile.close();
    }
}
