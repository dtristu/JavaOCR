package org.dtristu.javaocr.usersecurity.dto;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class DeleteAccountDTO {
    private String id;
    private String username;
    private String role;

    public DeleteAccountDTO(String id, String username, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        role="";
        authorities.forEach(e-> role = role + e.getAuthority() + " ");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
