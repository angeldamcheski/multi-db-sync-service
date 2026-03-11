package com.example.multidbsyncservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "synchronized_data",
        uniqueConstraints = @UniqueConstraint(columnNames = {"source_system", "source_document_id"}))
@Data
public class SynchronizedData {
    @Id
    private String id;

    @Column(name = "source_system", nullable = false)
    private String sourceSystem;

    @Column(name = "source_document_id"
            , nullable = false)
    private String sourceDocumentId;

    @Column(name = "document_number")
    private String documentNumber;

    @Column(name = "name")
    private String documentName;


    //TODO: Make these ENUMs
    @Column(name = "category")
    private String documentCategory;

    @Column(name = "status")
    private String documentStatus;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "last_modified_at")
    private String lastModifiedAt;

    @Column(name = "synced_at")
    private String syncedAt;
}
