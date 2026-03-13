package com.example.multidbsyncservice.repository;

import com.example.multidbsyncservice.entity.SynchronizedData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SynchronizedDataRepository extends JpaRepository<SynchronizedData, Long> {
    Optional<SynchronizedData> findBySourceSystemAndSourceDocumentId(String sourceSystem, String sourceDocumentId);

    @Query(value = "SELECT * FROM synchronized_data ORDER BY synced_at DESC LIMIT 50", nativeQuery = true)
    List<SynchronizedData> findRecentSyncs();

    List<SynchronizedData> findAllBySourceSystemAndSourceDocumentIdIn(String sourceSystem, List<String> ids);
}
