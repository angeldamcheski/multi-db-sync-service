package com.example.multidbsyncservice.dto;

import lombok.Data;

import java.util.Date;

@Data
public class SourceDocument {

    private String id;

    private String documentNumber;

    private String name;

    private String category;

    private String status;

    private Double amount;

    private String lastModifiedAt;
}
