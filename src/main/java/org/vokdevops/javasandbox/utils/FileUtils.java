package org.vokdevops.javasandbox.utils;

import java.io.File;

/**
 * Created by Vishakh Oommen Koshy on 28/02/2024
 */
public class FileUtils {
    public void checkFileOperations(){
        File f = new File("test.txt");

        // apply File class methods on File object
        System.out.println("File name :" + f.getName());
        System.out.println("Path: " + f.getPath());
        System.out.println("Absolute path:"
                + f.getAbsolutePath());
        System.out.println("Parent:" + f.getParent());
        System.out.println("Exists :" + f.exists());

        if (f.exists()) {
            System.out.println("Is writable:"
                    + f.canWrite());
            System.out.println("Is readable" + f.canRead());
            System.out.println("Is a directory:"
                    + f.isDirectory());
            System.out.println("File Size in bytes "
                    + f.length());
        }
    }
}
