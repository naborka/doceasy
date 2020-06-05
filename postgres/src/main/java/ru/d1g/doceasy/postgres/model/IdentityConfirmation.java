package ru.d1g.doceasy.postgres.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class IdentityConfirmation extends BaseEntity {
    @ManyToOne
    private Identity identity;
    @OneToOne
    private StringToken stringToken;

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    public StringToken getStringToken() {
        return stringToken;
    }

    public void setStringToken(StringToken stringToken) {
        this.stringToken = stringToken;
    }
}
