package org.dtristu.javaocr.user.dao;

import org.dtristu.javaocr.commons.dto.OCRTask;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "User")
public class Account {
    @MongoId
    private String id;
    private String firstName;
    private String lastName;
    private String userName;
    private String createdAt;
    private String password;
    @CreatedDate
    private LocalDate createdDate;
    private List<OCRTask> ocrTaskList;
    @Version
    private long version;
    public void addToOcrTaskList(OCRTask ocrTask){
        if (ocrTaskList==null){
            ocrTaskList=new ArrayList<>();
        }
        ocrTaskList.add(ocrTask);
    }

    public Account() {

    }

    public Account(String id, String firstName, String lastName, String userName, String createdAt, String password, LocalDate createdDate, List<OCRTask> ocrTaskList, long version) {
        this.version=version;
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.createdAt = createdAt;
        this.password = password;
        this.createdDate = createdDate;
        this.ocrTaskList = ocrTaskList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }
    public List<OCRTask> getOcrTaskList() {
        return ocrTaskList;
    }

    public void setOcrTaskList(List<OCRTask> ocrTaskList) {
        this.ocrTaskList = ocrTaskList;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}

