package com.example.multidbsyncservice.service.impl;

import com.example.multidbsyncservice.config.SourceDbConnection;
import com.example.multidbsyncservice.dto.SourceDocument;
import com.example.multidbsyncservice.entity.SyncMetadata;
import com.example.multidbsyncservice.entity.SynchronizedData;
import com.example.multidbsyncservice.repository.SyncMetadataRepository;
import com.example.multidbsyncservice.repository.SynchronizedDataRepository;
import com.example.multidbsyncservice.service.SyncEngineService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SyncEngineServiceImpl implements SyncEngineService {
    private final SyncMetadataRepository metadataRepository;
    private final SynchronizedDataRepository dataRepository;
    private final SourceDbConnection sourceDbConnection;

    @Value("${sync.db.prefix}")
    private String prefix;

    @Value("${sync.db.count}")
    private int dbCount;


    @Override
    public void syncAllSources() {
        System.out.println("[ENGINE] Starting global sync cycle...");
        for (int i = 1; i <= dbCount; i++) {
            String sourceName = prefix + "-" + i;
            try (Connection conn = sourceDbConnection.connect(sourceName)) {
                processSourceSequentially(sourceName, conn);
            } catch (Exception e) {
                System.err.println("[ERROR] Failed to sync " + sourceName + ": " + e.getMessage());
            }
        }
        System.out.println("[ENGINE] Global Sync Cycle Finished.");
    }

    private void processSourceSequentially(String sourceName, Connection conn) {
        // 1. Get the Bookmark (Metadata)
        SyncMetadata metadata = metadataRepository.findById(sourceName)
                .orElse(new SyncMetadata(sourceName, "1970-01-01T00:00:00Z", ""));

        String cursorTimestamp = metadata.getLastSuccessfulSync();
        String cursorId = metadata.getLastCursorId();
        int batchSize = 100;
        int totalProcessed = 0;

        while (true) {

            List<SourceDocument> batch = fetchBatchFromSource(conn, cursorTimestamp, cursorId,batchSize);

            if (batch.isEmpty()) break;

            List<String> ids = batch.stream().map(SourceDocument::getId).toList();

            List<SynchronizedData> existingDocs =
                    dataRepository.findAllBySourceSystemAndSourceDocumentIdIn(sourceName, ids);

            Map<String, SynchronizedData> existingMap = new HashMap<>();

            for (SynchronizedData doc : existingDocs) {
                existingMap.put(doc.getSourceDocumentId(), doc);
            }
            // 3. Process the batch (Upsert logic)
            List<SynchronizedData> entitiesToSave = new ArrayList<>();
            for (SourceDocument row : batch) {
                SynchronizedData entity = existingMap.get(row.getId());
                if(entity == null){
                    entity = new SynchronizedData();
                    entity.setId((UUID.randomUUID().toString()));
                    entity.setSourceDocumentId(row.getId());
                    entity.setSourceSystem(sourceName);
                }
                mapProperties(row, entity);
                entitiesToSave.add(entity);
                cursorTimestamp = row.getLastModifiedAt();
                cursorId = row.getId();
                System.out.println(
                        "[DEBUG] Fetching with cursor=" + cursorId +
                                " cursorId=" + cursorId
                );
            }

            // 4. Batch Save to Central DB (High Performance)
            dataRepository.saveAll(entitiesToSave);

            totalProcessed += batch.size();
            if (batch.size() < batchSize) break;
        }


        metadata.setLastSuccessfulSync(cursorTimestamp);
        metadata.setLastCursorId(cursorId);
        metadataRepository.save(metadata);

        if (totalProcessed > 0) {
            System.out.println("[SYNC] " + sourceName + " | Sync successful: " + totalProcessed + " records.");
        }
    }

    private List<SourceDocument> fetchBatchFromSource(Connection conn, String cursorTimestamp, String cursorId,int limit) {
        List<SourceDocument> batch = new ArrayList<>();

        System.out.println("[DEBUG] About to fetch batch from "  +
                " | cursorTimestamp=" + cursorTimestamp +
                " | cursorId=" + cursorId);
        String sql = "SELECT * FROM documents WHERE (last_modified_at > ?) OR (last_modified_at = ? AND id > ?) ORDER BY last_modified_at ASC, id ASC LIMIT ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cursorTimestamp);
            pstmt.setString(2, cursorTimestamp);
            pstmt.setString(3, cursorId);
            pstmt.setInt(4, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    SourceDocument doc = new SourceDocument();
                    doc.setId(rs.getString("id"));
                    doc.setDocumentNumber(rs.getString("document_number"));
                    doc.setName(rs.getString("name"));
                    doc.setCategory(rs.getString("category"));
                    doc.setStatus(rs.getString("status"));
                    doc.setAmount(rs.getDouble("amount"));


                    // Parse the SQLite string back into a Java Date
                    try {

                        doc.setLastModifiedAt(rs.getString("last_modified_at"));
                    } catch (Exception e) {
                        try {
                            doc.setLastModifiedAt(rs.getString("last_modified_at"));
                        } catch (Exception e2) {

                            doc.setLastModifiedAt(rs.getString("last_modified_at"));
                        }
                    }

                    batch.add(doc);
                }
            }
        } catch (Exception e) {
            System.err.println("[JDBC ERROR] Failed to fetch batch: " + e.getMessage());
            // Re-throw or handle based on your retry strategy
        }

        return batch;
    }

    private void mapProperties(SourceDocument dto, SynchronizedData entity) {
        entity.setDocumentName(dto.getName());
        entity.setDocumentCategory(dto.getCategory());
        entity.setDocumentStatus(dto.getStatus());
        entity.setAmount(dto.getAmount());
        entity.setDocumentNumber(dto.getDocumentNumber());
        entity.setLastModifiedAt(dto.getLastModifiedAt());
        entity.setSyncedAt(Instant.now().toString());
//        entity.setSourceSystem(dto.getId());
    }
}
