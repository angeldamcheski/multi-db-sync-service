package com.example.multidbsyncservice.config;

import org.hibernate.dialect.DatabaseVersion;
import org.hibernate.dialect.Dialect;

public class SQLiteDialect  extends Dialect {
    public SQLiteDialect(){
        super(DatabaseVersion.make(3));
    }
}
