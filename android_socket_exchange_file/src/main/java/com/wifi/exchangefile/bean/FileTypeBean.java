package com.wifi.exchangefile.bean;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class FileTypeBean implements Serializable {

    private int id;

    private String name;
    private ArrayList<File> files;

    public FileTypeBean(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<File> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<File> files) {
        this.files = files;
    }
}
