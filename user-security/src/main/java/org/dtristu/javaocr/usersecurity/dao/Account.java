package org.dtristu.javaocr.usersecurity.dao;

import org.dtristu.javaocr.commons.dto.OCRTask;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Document(collection = "User")
public class Account implements UserDetails {
    @MongoId
    private String id;
    private String firstName;
    private String lastName;
    @Indexed(unique = true)
    private String username;
    private String password;
    @CreatedDate
    private LocalDate createdDate;
    private List<OCRTask> ocrTaskList;
    @Version
    private long version;
    private List<GrantedAuthority> authorities;

    public void addToOcrTaskList(OCRTask ocrTask){
        if (ocrTaskList==null){
            ocrTaskList=new ArrayList<>();
        }
        ocrTaskList.add(ocrTask);
    }

    public Account() {

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
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


    public void setUsername(String username) {
        this.username = username;
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
    public void setAuthorities(List<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

}

