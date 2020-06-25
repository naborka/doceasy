package ru.d1g.doceasy.postgres.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Module extends BaseEntity implements HasOwner {
    private Boolean enabled;
    private String name;
    private String url;
    @ManyToOne
    private Account account;

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public Account getAccount() {
        return account;
    }

    @Override
    public void setAccount(Account account) {
        this.account = account;
    }
}
