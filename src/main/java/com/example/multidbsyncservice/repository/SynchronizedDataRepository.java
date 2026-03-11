package com.example.multidbsyncservice.repository;

import com.example.multidbsyncservice.entity.SynchronizedData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SynchronizedDataRepository extends JpaRepository<SynchronizedData, Long> {
    Optional<SynchronizedData> findBySourceSystemAndSourceDocumentId(String sourceSystem, String sourceDocumentId);
}
