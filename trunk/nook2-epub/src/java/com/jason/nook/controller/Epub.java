package com.jason.nook.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author jason
 */
public class Epub {

    public Epub() {
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InterruptedException {

//        String path = "/home/jason/Desktop/test/123456.epub";
//        String css = "/home/jason/Desktop/test/main.css";
//        unEpub(path, css);
//        zip(path.replace(".epub", ""), path.replace(".epub", "_new.epub"));
        String filePath = "/home/user192/Desktop/epub/linux.epub";
        File f= new File(filePath);
        File ff=new File(f.getPath()+"test");
        System.out.println(ff.getPath());
        System.out.println(ff.getName());
        System.out.println(f.renameTo(ff));

    }

    public static void Un(String path, String outName, String css) {
        try {
            unEpub(path, css);
            zip(path.replace(".epub", ""), path.substring(0, path.lastIndexOf("/")) + "/" + outName);
        } catch (Exception e) {
            System.err.println("[Un] " + e.getMessage());
        }
    }

    private static void unEpub(String path, String css) throws IOException, InterruptedException {
        File f = new File(path.replace(".epub", ""));
        f.mkdirs();

        ZipFile zf = new ZipFile(path);

        for (Enumeration entries = zf.entries(); entries.hasMoreElements();) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            String zipEntryName = entry.getName();

            File temp = new File(f.getAbsolutePath() + "/" + zipEntryName);

            if (entry.isDirectory()) {
                temp.mkdirs();
                continue;
            }

            if (!temp.exists()) {
                String p = temp.getPath();
                String pp = p.substring(0, p.lastIndexOf('/'));
                File ff = new File(pp);
                if (!ff.exists()) {
                    ff.mkdirs();
                }
                temp.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(temp);

            InputStream in = zf.getInputStream(entry);
            if (zipEntryName.endsWith(".css")) {
                in = new FileInputStream(new File(css));
            }
            int buff = 0;
            while ((buff = in.read()) != -1) {
                out.write(buff);
            }

            out.close();
            in.close();

        }
        zf.close();
    }

    private static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath);
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);
                delFolder(path + "/" + tempList[i]);
                flag = true;
            }
        }
        return flag;

    }

    public static void zip(String inputFileName, String outFile) {
        try {
            zip(outFile, new File(inputFileName));
            delFolder(inputFileName);
        } catch (Exception e) {
            System.err.println("[zip] " + e.getMessage());
        }
    }

    private static void zip(String zipFileName, File inputFile)
            throws FileNotFoundException, IOException {
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFileName));
        zip(zos, inputFile, "");
        zos.close();
    }

    private static void zip(ZipOutputStream zos, File f, String base)
            throws FileNotFoundException, IOException {
        BufferedInputStream bis = null;
        FileInputStream fis = null;
        if (f.isDirectory()) {
            File[] files = f.listFiles();

            zos.putNextEntry(new ZipEntry(base + "/"));

            base = base.length() == 0 ? "" : base + "/";
            for (int i = 0; i < files.length; i++) {
                zip(zos, files[i], base + files[i].getName());
            }
        } else {

            zos.putNextEntry(new ZipEntry(base));
            fis = new FileInputStream(f);
            bis = new BufferedInputStream(fis);
            byte[] b = new byte[1024 * 1024];
            int len = 0;
            while ((len = bis.read(b)) != -1) {
                zos.write(b, 0, len);
                zos.flush();
            }
            fis.close();
        }

    }
}
