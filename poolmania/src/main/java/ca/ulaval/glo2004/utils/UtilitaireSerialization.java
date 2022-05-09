package ca.ulaval.glo2004.utils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;

//Adaptation de https://mkyong.com/java/how-to-read-and-write-java-object-to-a-file/
public class UtilitaireSerialization {

    public static boolean serializeObject(String path, Serializable object) {
        try {
            FileOutputStream f = new FileOutputStream(path);
            ObjectOutputStream o = new ObjectOutputStream(f);

            // Write object to file
            o.writeObject(object);
            o.close();
            f.close();
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error initializing stream");
        }
        return false;
    }

    public static Object deserializeObject(String path) {
        try {
            FileInputStream fi = new FileInputStream(path);
            ObjectInputStream oi = new ObjectInputStream(fi);

            // Read object
            Object object = oi.readObject();
            oi.close();
            fi.close();

            return object;
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error initializing stream");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    //https://mkyong.com/java/java-serialization-examples/
    // Convert object to byte[]
    public static byte[] convertObjectToBytes(Object obj) {
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        try (ObjectOutputStream ois = new ObjectOutputStream(boas)) {
            ois.writeObject(obj);
            return boas.toByteArray();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        throw new RuntimeException();
    }

    //https://mkyong.com/java/java-serialization-examples/
    // Convert byte[] to object
    public static Object convertBytesToObject(byte[] bytes) {
        InputStream is = new ByteArrayInputStream(bytes);
        try (ObjectInputStream ois = new ObjectInputStream(is)) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException ioe) {
            ioe.printStackTrace();
        }
        throw new RuntimeException();
    }

    public static String ouvrirSaveTable(boolean ouvrirSave, Component parent){
        JFileChooser filePath = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Fichier Table (.tbl)", "tbl");
        filePath.setFileFilter(filter);
        int returnVal;
        if(ouvrirSave) {
            returnVal = filePath.showOpenDialog(parent);
        }else{
            returnVal = filePath.showSaveDialog(parent);
        }
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            String pathName = filePath.getSelectedFile().getPath();
            if (!pathName.endsWith(".tbl"))
                pathName += ".tbl";
            return pathName;
        }
        return null;
    }

    public static String saveSVG(Component parent){
        JFileChooser filePath = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "SVG (.svg)", "svg");
        filePath.setFileFilter(filter);
        int returnVal;
        returnVal = filePath.showSaveDialog(parent);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            String pathName = filePath.getSelectedFile().getPath();
            if (!pathName.endsWith(".svg"))
                pathName += ".svg";
            return pathName;
        }
        return null;
    }

    public static String savePNG(Component parent){
        JFileChooser filePath = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "PNG (.png)", "png");
        filePath.setFileFilter(filter);
        int returnVal;
        returnVal = filePath.showSaveDialog(parent);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            String pathName = filePath.getSelectedFile().getPath();
            if (!pathName.endsWith(".png"))
                pathName += ".png";
            return pathName;
        }
        return null;
    }

    public static String ouvrirImage(Component parent){
        JFileChooser filePath = new JFileChooser();
        FileNameExtensionFilter filterJPG = new FileNameExtensionFilter(
                "Fichier JPG", "jpg");
        FileNameExtensionFilter filterPNG = new FileNameExtensionFilter(
                "Fichier PNG", "png");
        filePath.setFileFilter(filterPNG);
        filePath.addChoosableFileFilter(filterJPG);
        int returnVal;
        returnVal = filePath.showOpenDialog(parent);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            String pathName = filePath.getSelectedFile().getPath();
            return pathName;
        }
        return null;
    }

}
