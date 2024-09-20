package com.example.LibraryOCRJava;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class OCRTask implements Serializable {
    private String documentId;
    private Set<String> rawImagesId;
    private Set<String> processedImagesId;
    private Set<String> ocrResults;
    private String mergedResult;
    private List<String> log;
    private String userName;

    public OCRTask() {
    }

    public void addToLog(String string){
        if (log==null){
            log= new ArrayList<>();
        }
        log.add(string);
    }


    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public Set<String> getRawImagesId() {
        return rawImagesId;
    }

    public void setRawImagesId(Set<String> rawImagesId) {
        this.rawImagesId = rawImagesId;
    }

    public Set<String> getProcessedImagesId() {
        return processedImagesId;
    }

    public void setProcessedImagesId(Set<String> processedImagesId) {
        this.processedImagesId = processedImagesId;
    }

    public Set<String> getOcrResults() {
        return ocrResults;
    }

    public void setOcrResults(Set<String> ocrResults) {
        this.ocrResults = ocrResults;
    }

    public String getMergedResult() {
        return mergedResult;
    }

    public void setMergedResult(String mergedResult) {
        this.mergedResult = mergedResult;
    }

    public List<String> getLog() {
        return log;
    }

    public void setLog(List<String> log) {
        this.log = log;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OCRTask ocrTask = (OCRTask) o;
        return Objects.equals(getDocumentId(), ocrTask.getDocumentId()) && Objects.equals(getRawImagesId(), ocrTask.getRawImagesId()) && Objects.equals(getProcessedImagesId(), ocrTask.getProcessedImagesId()) && Objects.equals(getMergedResult(), ocrTask.getMergedResult()) && Objects.equals(getLog(), ocrTask.getLog()) && Objects.equals(getUserName(), ocrTask.getUserName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getDocumentId());
    }
}

