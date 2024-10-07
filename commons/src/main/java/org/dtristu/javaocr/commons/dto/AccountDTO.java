package org.dtristu.javaocr.commons.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Account DTO for usage between microservices
 */
public class AccountDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String userName;
    private List<OCRTask> ocrTaskList;

    public void addToOcrTaskList(OCRTask ocrTask) {
        if (ocrTaskList == null) {
            ocrTaskList = new ArrayList<>();
        }
        ocrTaskList.add(ocrTask);
    }

    public AccountDTO() {
    }

    public AccountDTO(String id, String firstName, String lastName, String userName, List<OCRTask> ocrTaskList) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
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

    public List<OCRTask> getOcrTaskList() {
        return ocrTaskList;
    }

    public void setOcrTaskList(List<OCRTask> ocrTaskList) {
        this.ocrTaskList = ocrTaskList;
    }
}
