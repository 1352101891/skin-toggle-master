package com.skinlibrary.entity;

import android.content.res.Resources;

public class ResourceBo {
    private Resources resources;
    private String packName;
    private String sourcePath;

    public ResourceBo(Resources resources, String packName, String sourcePath) {
        this.resources = resources;
        this.packName = packName;
        this.sourcePath = sourcePath;
    }

    public ResourceBo() {
    }

    public Resources getResources() {
        return resources;
    }

    public String getPackName() {
        return packName;
    }

    public String getSourcePath() {
        return sourcePath;
    }
}
