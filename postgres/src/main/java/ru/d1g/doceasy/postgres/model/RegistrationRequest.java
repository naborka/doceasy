package ru.d1g.doceasy.postgres.model;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class RegistrationRequest extends BaseEntity {
    private String personalName;
    private String email;
    private String password;
    @OneToOne
    private IdentityConfirmation identityConfirmation;

    public String getPersonalName() {
        return personalName;
    }

    public void setPersonalName(String personalName) {
        this.personalName = personalName;
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

    public IdentityConfirmation getIdentityConfirmation() {
        return identityConfirmation;
    }

    public void setIdentityConfirmation(IdentityConfirmation identityConfirmation) {
        this.identityConfirmation = identityConfirmation;
    }
}
