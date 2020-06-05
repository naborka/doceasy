package ru.d1g.doceasy.mongo.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.time.Instant;

@Document
@EntityListeners(AuditingEntityListener.class)
public class DocumentEntity {
    @Id
    private String id;
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Instant createdDate;
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Instant lastModified;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getLastModified() {
        return lastModified;
    }

    public void setLastModified(Instant lastModified) {
        this.lastModified = lastModified;
    }
}
