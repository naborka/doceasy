package ru.d1g.doceasy.postgres.model;

public interface HasOwner {
    Account getAccount();
    void setAccount(Account account);
}
