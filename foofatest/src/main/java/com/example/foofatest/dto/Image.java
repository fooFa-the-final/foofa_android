package com.example.foofatest.dto;

import java.io.Serializable;

/**
 * Created by kosta on 2017-06-12.
 */

public class Image implements Serializable{
    private String imageId;
    private String category;
    private String categoryId;
    private String filename;

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
