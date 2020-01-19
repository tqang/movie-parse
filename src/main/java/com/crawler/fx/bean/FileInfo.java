package com.crawler.fx.bean;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FileInfo extends MoveInfo {

    private String path;

    private String originalFileName;

    private String fileName;

    /**
     * 是否是多部分组成
     */
    private Boolean isSplit = false;

    /**
     * 处理状态：准备、处理中、解析成功、移动成功、解析失败、移动失败
     */
    private StringProperty status = new SimpleStringProperty();

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public boolean isSplit() {
        return isSplit;
    }

    public void setSplit(boolean split) {
        isSplit = split;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "path='" + path + '\'' +
                ", originalFileName='" + originalFileName + '\'' +
                ", fileName='" + fileName + '\'' +
                ", isSplit=" + isSplit +
                ", status=" + status +
                '}';
    }
}
