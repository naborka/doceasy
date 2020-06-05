package ru.d1g.doceasy.postgres.model;

import javax.persistence.Entity;

@Entity
public class Account extends BaseEntity {
    private Boolean active;
    private String email;
    private String password;

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
