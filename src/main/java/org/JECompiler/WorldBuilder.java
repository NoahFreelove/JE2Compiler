package org.JECompiler;

import JE.Window.WindowPreferences;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
public class WorldBuilder {

    public static void buildAllWorlds(String sceneFolderDirectory, String runPath, String runPackage, WindowPreferences windowPreferences){
        System.out.println("Starting to build JEMain.java");
        String[] allFiles = Compiler.getFiles(new File(sceneFolderDirectory));
        ArrayList<String> files = new ArrayList<>();
        for (String path :
                allFiles) {
            if(path.endsWith(".JEScene")){
                files.add(new File(path).getName());
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(runPackage).append(";").append(System.lineSeparator());
        sb.append("import JE.Scene.Scene;").append(System.lineSeparator());
        sb.append("import JE.Window.WindowPreferences;").append(System.lineSeparator());
        sb.append("import JE.Manager;").append(System.lineSeparator());
        sb.append("import org.joml.Vector2i;").append(System.lineSeparator());
        sb.append("public class JEMain {").append(System.lineSeparator());
        sb.append("public static void main(String[] args) {").append(System.lineSeparator());
        sb.append("Manager.start(new WindowPreferences(new Vector2i(").append(windowPreferences.windowSize.x).append(", ").append(windowPreferences.windowSize.y).append("), \"").append(windowPreferences.windowTitle).append("\", ").append(windowPreferences.windowResizable).append(", ").append(windowPreferences.vSync).append("));").append(System.lineSeparator());

        int i = 0;
        for (String str: files.toArray(new String[0])){
            sb.append("Scene scene").append(i).append(" = new Scene().load(\"").append(str).append("\");").append(System.lineSeparator());
            sb.append("Manager.addBuildScene(scene").append(i).append(");").append(System.lineSeparator());
            i++;
        }
        sb.append("Manager.setScene(0);").append(System.lineSeparator());
        sb.append("}").append(System.lineSeparator());
        sb.append("}").append(System.lineSeparator());

        File file = new File(runPath + "/JEMain.java");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.append(sb);
            System.out.println("Successfully created JEMain.java file at: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
}
