package com.example.multidbsyncservice.repository;

import com.example.multidbsyncservice.entity.SyncMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SyncMetadataRepository extends JpaRepository<SyncMetadata, String> {
}
