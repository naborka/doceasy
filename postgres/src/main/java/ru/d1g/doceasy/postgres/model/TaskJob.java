package ru.d1g.doceasy.postgres.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class TaskJob extends BaseEntity {
    @ManyToOne
    private Account account;
    @OneToOne
    private Module module;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> imageIds = new HashSet<>();

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public Set<String> getImageIds() {
        return imageIds;
    }

    public void setImageIds(Set<String> imageIds) {
        this.imageIds = imageIds;
    }
}
