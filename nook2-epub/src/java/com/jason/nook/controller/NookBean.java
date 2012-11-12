package com.jason.nook.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import sun.jdbc.odbc.OdbcDef;

/**
 *
 * @author jason
 */
@ManagedBean
@ViewScoped
public class NookBean implements Serializable {

    private final static String finalPath = getFileSavePath();
    private static String css = finalPath + "/main.css";
    private List<StreamedContent> downloads;

    public NookBean() {
    }

    private static String getFileSavePath() {
        String reqPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
        File file = new File(reqPath + "/upload/");
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            return file.getCanonicalPath();
        } catch (IOException ex) {
            Logger.getLogger(NookBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void fileUpload(FileUploadEvent event) throws IOException {
        UploadedFile f = event.getFile();
        InputStream in = f.getInputstream();

        File file = new File(finalPath + "/" + f.getFileName().replace(".epub", date() + "original.epub"));
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream out = new FileOutputStream(file);

        int buff = 0;
        while ((buff = in.read()) != -1) {
            out.write(buff);
        }
        System.out.println(file.getAbsolutePath());

        String fp = file.getPath();
        String newFileName = f.getFileName().replace(".epub", date() + ".epub");
        Epub.Un(fp, newFileName, css);
        addDownload(f.getFileName(), newFileName);
        in.close();
        out.close();
    }

    private void addDownload(String oldName, String newName) {
        
            InputStream stream = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("/upload/" + newName);
            
            downloads.add(new DefaultStreamedContent(stream, "image/jpg", oldName));
            System.err.println("oldName: "+oldName+"");
            
            System.err.println("oldName: "+Charset.isSupported("UTF-8"));


            File f = new File(finalPath + "/" + newName);
            if (f.exists()) {
                f.delete();
            }
    }

    public List<StreamedContent> getDownloads() {
        if (downloads == null) {
            downloads = new ArrayList<StreamedContent>();
        }
        return downloads;
    }

    public void setDownloads(List<StreamedContent> downloads) {
        this.downloads = downloads;
    }

    private String date() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return format.format(new Date());
    }
}
