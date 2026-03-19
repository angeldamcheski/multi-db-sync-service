# Data Sync Engine (Spring Boot)

A lightweight, high-performance synchronization service designed to aggregate data from multiple distributed source databases into a centralized monitoring repository.  
The engine uses a **checkpoint-based cursor system** to ensure data consistency and incremental updates.

## 🚀 Overview

The Sync Engine is built to solve the **"Distributed Source"** problem.  
It connects to *N* number of source databases (prefixed in configuration), fetches modified records in batches, and performs an **upsert** (Update or Insert) operation in the central repository.

### Key Features

- **Incremental Sync** — Uses `last_modified_at` + `id` cursors to pick up exactly where it left off
- **Batch Processing** — Fetches and saves data in chunks (default: 100) to keep memory footprint low
- **Real-time Dashboard** — Thymeleaf-based UI with live stats on throughput, categories and sync health
- **Asynchronous Execution** — Trigger sync cycles via REST without blocking the main thread

## 🛠 Tech Stack

- **Backend**: Java 21, Spring Boot 3.x, Spring Data JPA
- **Database**: Multi-source JDBC connections (SQLite / PostgreSQL / MySQL compatible)
- **Frontend**: Thymeleaf + JavaScript
- **Utility**: Lombok, `CompletableFuture` for async tasks

## 📖 How it Works

### 1. The Cursor Logic

To avoid re-processing millions of rows, the engine tracks a `SyncMetadata` object for every source system.

The SQL query uses a **tie-breaker** approach to ensure no records are missed when timestamps are equal:

```sql
WHERE (last_modified_at > ?)
   OR (last_modified_at = ? AND id > ?)

```
###   2. The Sync Flow

1. #### **Identify Sources**

Iterates from 1 to ```dbCount``` using configurable prefix (```source-db```)

2. #### **Fetch Metadata**

Retrieves the last successful timestamp and ID for that specific source

3. #### **Batch Fetch**

Queries the source DB for records newer than the cursor


4. #### **Upsert**
* Checks if record exists in central DB
* Maps properties from ```SourceDocument``` → ```SynchronizedData```
* Generates new UUID for inserts or updates existing record

5. #### **Commit Checkpoint**
Updates ```SyncMetadata``` with the latest timestamp from the processed batch

## ⚙️ Configuration
Add these properties to application.properties:
```java 
properties

# Sync Configuration
sync.db.prefix=source-db
sync.db.count=5

# Central DB Configuration (Standard Spring Data)
spring.datasource.url=jdbc:postgresql://localhost:5432/central_db
spring.jpa.hibernate.ddl-auto=update
```
## 🖥 API & Dashboard
### REST Endpoints


| Method | Endpoint | Description | 
--------- | -------- | ------- |
| POST | ```/run/all``` |Triggers asynchronous sync of all databases| 
GET | ```/dashboard``` |Returns the Thymeleaf monitoring view

### Dashboard Features

* Live Clock (synchronized with server time)
* Next Sync Countdown (60-second sync window indicator)
* Batch Statistics (Created vs Updated count)
* Financial Throughput (aggregate ```amount``` in current batch)
* Status Distribution (Active vs Inactive documents)

## 📂 Project Structure (main classes)

* ```SyncEngineServiceImpl.java```

→ Core orchestration logic and JDBC batch fetching
* ```SynchronizedData.java```

→ JPA Entity representing the centralized record
* ```SyncMetadata.java```

→ Entity storing checkpoints (bookmarks) per source
* ```DashboardController.java```

→ Server-side logic for the monitoring UI
