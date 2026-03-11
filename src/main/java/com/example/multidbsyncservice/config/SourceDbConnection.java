package com.example.multidbsyncservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class SourceDbConnection {
    @Value("${sync.db.directory}")
    private String databaseDirectory;

    public Connection connect(String sourceName) throws SQLException{
        String dbPath = databaseDirectory + "/"+sourceName+".db";
        String url = "jdbc:sqlite:"+dbPath;
        return DriverManager.getConnection(url);
    }
}
