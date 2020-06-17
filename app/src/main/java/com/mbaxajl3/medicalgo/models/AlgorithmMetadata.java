package com.mbaxajl3.medicalgo.models;

import java.io.Serializable;

public class AlgorithmMetadata implements Serializable {
    private String algorithmName;
    private int algorithmId;
    private int categoryId;
    private String filename;
    private String imagePath;

    public AlgorithmMetadata(String algorithmName, int algorithmId, int categoryId, String filename, String imagePath) {
        this.algorithmName = algorithmName;
        this.algorithmId = algorithmId;
        this.categoryId = categoryId;
        this.filename = filename;
        this.imagePath = imagePath;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public int getAlgorithmId() {
        return algorithmId;
    }

    public void setAlgorithmId(int algorithmId) {
        this.algorithmId = algorithmId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
