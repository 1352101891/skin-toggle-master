package com.skinlibrary.entity;

public class SourceBo {
    public enum TYPE{ LOCAL ,NET,ASSETS;
        public static boolean contains(TYPE type){
            for(TYPE typeEnum : TYPE.values()){
                if(typeEnum ==type ){
                    return true;
                }
            }
            return false;
        }
    }

    private String name;
    private String path;
    private TYPE type;
    private String coverImage;
    private String Url;


    public SourceBo(String name, String path, TYPE type) {
        this.name = name;
        this.path = path;
        this.type=type;
    }

    public SourceBo(String name, String path,TYPE type,String coverImage) {
        this.name = name;
        this.path = path;
        this.type = type;
        this.coverImage = coverImage;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }
}
