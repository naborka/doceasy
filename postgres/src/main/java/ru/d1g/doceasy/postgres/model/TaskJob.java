package ru.d1g.doceasy.postgres.model;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.util.HashSet;
import java.util.Set;

@Entity
public class TaskJob extends BaseEntity implements HasOwner {
    @ManyToOne
    private Account account;
    private String name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
