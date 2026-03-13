package com.example.multidbsyncservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "sync_metadata")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SyncMetadata {
    @Id
    @Column(name = "source_system")
    private String sourceId;

    @Column(name = "last_successful_sync")
    private String lastSuccessfulSync;

    @Column(name = "last_cursor_id")
    private String lastCursorId;
}
